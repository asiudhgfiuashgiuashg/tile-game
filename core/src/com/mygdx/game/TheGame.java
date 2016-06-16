package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.ai.Agent;
import com.mygdx.ai.PositionIndexedNode;
import com.mygdx.ai.TestAi;
import com.mygdx.game.listeners.InLobbyMessageTextFieldListener;
import com.mygdx.game.listeners.InventoryButtonListener;
import com.mygdx.game.lobby.CurrentClass;
import com.mygdx.game.lobby.LobbyManager;
import com.mygdx.game.lobby.LobbyPlayer;
import com.mygdx.game.player.MageClass;
import com.mygdx.game.player.Player;
import com.mygdx.game.player.RangerClass;
import com.mygdx.game.player.ShieldClass;
import com.mygdx.game.player.class_input_processors.MageInputProcessor;
import com.mygdx.game.player.class_input_processors.RangerInputProcessor;
import com.mygdx.game.player.class_input_processors.ShieldInputProcessor;
import com.mygdx.game.serializers.GameMapSerializer;
import com.mygdx.server.Server;


public class TheGame extends ApplicationAdapter {
	SpriteBatch batch;
	
	public static GameMap currentMap;

	/**
	 * contains UI resources such as textures, colors, fonts
	 */
    private Skin skin;
    /**
     * responsible for laying out and displaying the GUI
     */
    private ExtendedStage stage;

    
    
    
    private ObjectShape playerShape;

    
    
    
    
    protected static enum GameState {
    	MAIN_MENU,
        SERVER_CONNECT_SCREEN,
        SERVER_HOST_SCREEN,
        IN_LOBBY,
        GAME_STARTED,
    }
    
    /**
     * represents the screen that the game is in (main menu, connecting to server screen, lobby screen, in game, etc ..)
     */
    static GameState gameState;
    
    /**
     *  the inputmultiplexer will first give events to the gui input processor
     * Events which are unprocessed by the gui input processor will be given to the gameInputProcessor.
     * Events which are unprocessed by the gameInputProcessor will be given to thhe classSpecificProcessor to handle class-specific input behaviors
     */
    private InputMultiplexer inputMultiplexer;
    private GameInputProcessor gameInputProcessor;
    private InputProcessor classSpecificInputProcessor;
    
    /**
     * if this client is also hosting, this field will be used to refer to the server
     */
    private Server server;
    /**
     * whethhr thhis client is also hosting or not
     */
    private boolean hosting;
    /**
     * if true, lines are drawn to debug various things like hitboxes and pathfinding graphs
     */
    public static boolean debug;
    
    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 480;
    
    /**
     * manages the information in the lobby
     */
    private LobbyManager lobbyManager;


    /**
     * used to communicate with the server
     */
	private final Communicator communicator = new Communicator(this);

	/**
	 * contains the username of the player starting when the player hits the connect button
	 * we need to keep this around so that the stage can reference it
	 */
	private String username;

	
	
    
	@Override
	public void create() {
		debug = false;
		
		setupSkin();
		
		
		gameState = GameState.SERVER_CONNECT_SCREEN;
		batch = new SpriteBatch();
		
		setupStage();
		
		setupMultiplexer();
		

		/*
		 * the hitbox of the player - in the future this will need to be moved elsewhere
		 */
		playerShape = new ObjectShape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 55)),
				new LineSeg(new Point(15, 55), new Point(50, 55)),
				new LineSeg(new Point(50, 55), new Point(50, 0)),
				new LineSeg(new Point(50, 0), new Point(15, 0))
				),
				new Point(0,0));
		
		
		setupMainMenu();
	}
	
	/**
	 * set up the input classes for the game
	 * the two input processors are the stage and then the gameInputProcessor
	 * the stage receives input first and decides whether it should be passed on to the gameInputProcessor
	 * input to the stage is for the gui and input to the gameInputProcessor is to control the character
	 */
	private void setupMultiplexer() {
		//set up input processors (stage and gameInputProcessor) and add them to the multiplexer
		// stage should get events first and then possibly gameInputProcessor
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		
		/*
		 * the inputMultiplexer will pass unhandled events along to the next inputprocessor
		 *  event ----given to -----> stage ---- event was unhandled ----- given to -----> gameInputProcessor
		 */
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	/**
	 * create the stage upon which GUI elements will be drawn
	 */
	private void setupStage() {
		
		/*
		 * the stage is given the communicator so that changes that the server needs to know about 
		 *  can be encoded by the communicator and sent
		 */
		stage = new ExtendedStage(skin, this, communicator ); //the gui is laid out here
		
		/*
		 * give the communicator the stage so that the communicator can modify the stage according to network messages from the server
		 *  (translate network messages to changes in the view)
		 */
		communicator.setStage(stage);
		stage.setDebugAll(debug);
		/*
		 * give the stage access to the resources contained within the skin
		 */
		stage.skin = skin;
	}
	
	/**
	 * sets up a skin
	 * a skin stores resources for the UI widgets to use (texture regions, fonts, colors, etc)
	 * resources are named and can be looked up by name and type
	 * 
	 * more here: https://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/scenes/scene2d/ui/Skin.html
	 */
	private void setupSkin() {
		//setup the skin (resources for gui)
		skin = new Skin();
		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		skin.add("default", new BitmapFont());
		
		
		ListStyle listStyle = new ListStyle();
		listStyle.font = skin.getFont("default");
		listStyle.fontColorSelected = Color.WHITE;
		listStyle.fontColorUnselected = Color.LIGHT_GRAY;
		listStyle.selection = skin.newDrawable("white", Color.FIREBRICK);
		skin.add("default", listStyle);
		
		//register a small label style with skin for use in inventory and stuff
		BitmapFont smallFont = new BitmapFont();
		smallFont.getData().setScale(0.7f);
		LabelStyle smallLabel = new LabelStyle();
		smallLabel.font = smallFont;
		skin.add("small", smallLabel);
	}
	
	private void setupMainMenu() {
		stage.setupMainMenu();
	}
	
	protected void setupLobbyAsHost(String username) {
		server = new Server(8080, stage);
		hosting = true;
		gameState = GameState.IN_LOBBY;
		connectToServer("localhost", 8080, username);
		setupLobby();
	}

	/**
	 * set up the stage for the lobby and spawn a lobby manager to help manage the lobby information (players connected and stuff)
	 */
	void setupLobby() {
		this.lobbyManager = new LobbyManager();
		stage.setupLobby();
	}
	
	//attempts to connect to server, returns true for success
	protected boolean connectToServer(String serverAddress, int port, String username) {
		this.setUsername(username);
		try {
			communicator.connectToServer(InetAddress.getByName(serverAddress), port, username);
		} catch (UnknownHostException e) {
			stage.displayError();
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	/**
	 * create players and map
	 */
	private void setupForInGame() {
		batch = new SpriteBatch();
		
		
		Scanner sc = new Scanner(System.in);
        ///System.out.println("Which map would you like to test?");
        String mapName = "test";//sc.nextLine();
        
        sc.close();
        

		Json json = new Json();
		json.setSerializer(GameMap.class, new GameMapSerializer());
        currentMap = json.fromJson(GameMap.class, Gdx.files.internal(mapName + ".json"));
        /*
         * since we are on a client and the map will need to draw itself, call this function to set it up to do that
         */
        currentMap.setupMapForClient(batch);

        if (hosting) {
        	currentMap.initializeGraph();
        	if (debug) {
        		TestAi testAi = new TestAi(playerShape, true, currentMap);
                testAi.setFollowPlayer(currentMap.localPlayer, true);
                currentMap.agents.add(testAi);
        	}
             
             //server.gameMap = currentMap;
        }
        stage.currentMap = currentMap;

		
		/*
		 * convert lobbyplayers to players
		 */
		for (LobbyPlayer lobbyPlayer: lobbyManager.getLobbyPlayers()) {
			Player player = getPlayer(lobbyPlayer);
			currentMap.addPlayer(player);
			/*
			 * keep track of the local player since the gameMap needs to know who the local player is to display correctly
			 */
			if (lobbyPlayer.equals(lobbyManager.getLocalLobbyPlayer())) {
				currentMap.localPlayer = player;
			}
		}
		
		//player.create(); responsibilities for create() moved to constructor
		//localPlayer.setFOV(localPlayer.sightX, localPlayer.sightY);
	}
	
	/**
	 * A conversion method.
	 * takes lobby player when the lobby is ending
	 *  and returns an instance of the appropriate subclass of Player
	 * @param className the name of the class from the network class assignment message
	 * @return an instance of the appropriate subclass of Player
	 */
	private Player getPlayer(LobbyPlayer thePlayer) {
		System.out.println("lobbyplayer uid: " + thePlayer.getUid());
		CurrentClass theClass = thePlayer.getCurrentClass();
		if (theClass == CurrentClass.RANGER) {
			return new RangerClass(thePlayer.getUid());
		} else if (theClass == CurrentClass.MAGE) {
			return new MageClass(thePlayer.getUid());
		} else if (theClass == CurrentClass.SHIELD) {
			return new ShieldClass(thePlayer.getUid());
		}
		throw new IllegalArgumentException("unsupported class!" + " You tried to use the following class: " + theClass);
	}

	@Override
	public void render()
	{
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (GameState.GAME_STARTED == gameState) {
			batch.begin();

			currentMap.draw(batch); //draw things which don't cast shadows (tiles)
			

			batch.end();
			
			currentMap.update();
			
			/*
			 * have the gameinputprocessor move the player every frame according to wsad input
			 */
			try {
				gameInputProcessor.moveLocalPlayer(Gdx.graphics.getDeltaTime());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			 * send the position and direction of the local player to the server so that it can distribute them to everyone
			 */
			communicator.sendLocalPlayerPosition();
			communicator.sendLocalPlayerDirection();
			
			
			
			if (debug) {
				currentMap.debugGraph(); //MUST COME AFTER BATCH, batch and shaperenderer cannot mix
				currentMap.debugShapes();
				currentMap.debugAgents();
			}
			
			
			
			for (Player player: currentMap.players) {
				if (player != currentMap.localPlayer) { //currentMap.player = this.player btw
					//adjust label position for remote players
					float xOffset = player.getWidth() / 2 - player.nameLabel.getWidth() / 2;


					player.nameLabel.setPosition((float) (player.getPos().getX() - currentMap.mapPosX) + xOffset, (float) (player.getPos().getY() - currentMap.mapPosY) - 18);
				}
			}
		}
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		
		doNetworking();
	}
	
	/**
	 * communicate with the server and if hosting.
	 * have the server do communication if you are hosting a server.
	 */
	private void doNetworking() {	
		communicator.receiveMessage();
		if (hosting) {
			if (GameState.IN_LOBBY == gameState) {
				server.checkForConnections();
			}
			server.dealWithMessages();
		}
				//**end networking******
	}
				
				
	public void addInGameActors() {
		stage.clear();
		stage.addInGameActors();
	}
	
	 /*
	  * the inputmultiplexer will first give events to the gui input processor
	  * Events which are unprocessed by the gui input processor will be given to the gameInputProcessor.
      * Events which are unprocessed by the gameInputProcessor will be given to thhe classSpecificProcessor to handle class-specific input behaviors
	  * thus thhere must be an inputprocessor for each class
	  */
    
	private void setupInGameInputProcessors() throws Exception {
		gameInputProcessor = new GameInputProcessor(currentMap.localPlayer);
		inputMultiplexer.addProcessor(gameInputProcessor);
		/*
		 * now add the per-class input processor which will handle class(mage, rangger, shield)-specific inputs
		 */
		Player localPlayer = currentMap.localPlayer;
		if (localPlayer instanceof RangerClass) {
			classSpecificInputProcessor = new RangerInputProcessor();
		} else if (localPlayer instanceof MageClass) {
			classSpecificInputProcessor = new MageInputProcessor();
		} else if (localPlayer instanceof ShieldClass) {
			classSpecificInputProcessor = new ShieldInputProcessor();
		} else {
			throw new Exception("the local player has some class for which there is no class-specific input processor hooked up");
		}
		inputMultiplexer.addProcessor(classSpecificInputProcessor);
	}
	
	public LobbyManager getLobbyManager() {
		return lobbyManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * setup the stage and the map for in game
	 */
	public void transitionFromLobbyToInGame() {
		TheGame.gameState = GameState.GAME_STARTED;
		setupForInGame();
		addInGameActors();
		try {
			setupInGameInputProcessors();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*Player addRemotePlayerToList(String playerName, int uid) {
		Player remotePlayer = new Player(playerShape, true);
		remotePlayer.uid = uid;
		remotePlayer.username = playerName;
		currentMap.players.add(remotePlayer);
		remotePlayer.setPos(new Point(-100, -100));
		return remotePlayer;
	}*/
}
