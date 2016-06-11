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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.ai.Agent;
import com.mygdx.ai.PositionIndexedNode;
import com.mygdx.ai.TestAi;
import com.mygdx.game.listeners.InLobbyMessageTextFieldListener;
import com.mygdx.game.listeners.InventoryButtonListener;
import com.mygdx.server.Server;


public class TheGame extends ApplicationAdapter {
	SpriteBatch batch;
	
	public static GameMap currentMap;
    private long time;
    private final int SEND_SPACING = 50;
    private DirectionOfTravel playerOldDirection;
    private Skin skin;
    private ExtendedStage stage;

    
    
    
    private Shape playerShape;
    private Point oldPos;
    
    
    
    protected LocalPlayer localPlayer;
    
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
    
    private InputMultiplexer inputMultiplexer; //will delegate events tos the game inputprocessor and the gui inputprocessor (the stage)
    private GameInputProcessor gameInputProcessor;
    
    private Server server;
    private boolean hosting;
    public static boolean debug;
    
    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 480;
    
    private ShaderProgram shaderProgram;
    Texture testImg;

    /**
     * used to communicate with the server
     */
	private final Communicator communicator = new Communicator(this);
    
	@Override
	public void create() {
		debug = false;
		testImg = new Texture("badlogic.jpg");
		
		//set up the shaders
		shaderProgram = new ShaderProgram(Gdx.files.internal("data" + File.separator + "default.vert"), 
				Gdx.files.internal("data" + File.separator + "grayscale.frag"));
		if (!shaderProgram.isCompiled()) 
			throw new GdxRuntimeException("Couldn't compile shader: " + shaderProgram.getLog());
		
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
		
		
		oldPos = new Point(0, 0);
		gameState = GameState.SERVER_CONNECT_SCREEN;
		batch = new SpriteBatch();
		
		//set up input processors (stage and gameInputProcessor) and add them to the multiplexer
		// stage should get events first and then possibly gameInputProcessor
		stage = new ExtendedStage(skin, this, communicator ); //the gui is laid out here
		communicator.setStage(stage);
		stage.setDebugAll(debug);
		stage.skin = skin;
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		
		Gdx.input.setInputProcessor(inputMultiplexer); //the stage which contains the gui/hud gets to handle inputs first, and then pass the ones it doesn't handle down to the game
		
		playerShape = new Shape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 55)),
				new LineSeg(new Point(15, 55), new Point(50, 55)),
				new LineSeg(new Point(50, 55), new Point(50, 0)),
				new LineSeg(new Point(50, 0), new Point(15, 0))
				),
				new Point(0,0));
		
		
		setupMainMenu();
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


	private void setupLobby() {
		stage.setupLobby();
	}
	
	//attempts to connect to server, returns true for success
	protected boolean connectToServer(String serverAddress, int port, String username) {

		try {
			communicator.connectToServer(InetAddress.getByName(serverAddress), port, username);
		} catch (UnknownHostException e) {
			stage.displayError();
			e.printStackTrace();
		}
		
		
		localPlayer = new LocalPlayer(playerShape, false, communicator);
		localPlayer.username = username;
		setupForInGame();
		
		return true;
	}
	
	
	/**
	 * create player and map
	 */
	private void setupForInGame() {
		batch = new SpriteBatch();
		
		
		Scanner sc = new Scanner(System.in);
        ///System.out.println("Which map would you like to test?");
        String mapName = "test";//sc.nextLine();
        
        sc.close();
        
		try {
            ///System.out.println("Working Directory = " + System.getProperty("user.dir"));

            currentMap = new GameMap(Gdx.files.internal(mapName + ".json"), localPlayer, batch);

            if (hosting) {
            	currentMap.initializeGraph();
            	if (debug) {
            		TestAi testAi = new TestAi(playerShape, true, currentMap);
	                testAi.setFollowPlayer(localPlayer, true);
	                currentMap.agents.add(testAi);
            	}
                 
                 server.gameMap = currentMap;
            }
            stage.currentMap = currentMap;
            
        }
        catch(IOException e) {
        	System.out.println("Failed to create map object");
            e.printStackTrace();
        }
		
		//player.create(); responsibilities for create() moved to constructor
		localPlayer.setFOV(localPlayer.sightX, localPlayer.sightY);
		
		//initialize various GuiManagers, giving them appropriate GuiElements
		
		time = System.currentTimeMillis();
	}

	@Override
	public void render()
	{
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (GameState.GAME_STARTED == gameState) {
			batch.begin();
			//batch.setShader(shaderProgram);
			currentMap.draw(batch); //draw things which don't cast shadows (tiles)
			
			//batch.draw(testImg, 0, 0);
			batch.end();
			
			currentMap.update();
			
			
			if (debug) {
				currentMap.debugGraph(); //MUST COME AFTER BATCH, batch and shaperenderer cannot mix
				currentMap.debugShapes();
				currentMap.debugAgents();
			}
			
			
			
			for (Player player: currentMap.players) {
				if (player != currentMap.player) { //currentMap.player = this.player btw
					//adjust label position for remote players
					float xOffset = player.getWidth() / 2 - ((RemotePlayer) player).nameLabel.getWidth() / 2;

					//System.out.println(xOffset);

					((RemotePlayer) player).nameLabel.setPosition((float) (player.getPos().getX() - currentMap.mapPosX) + xOffset, (float) (player.getPos().getY() - currentMap.mapPosY) - 18);
				}
			}
		}
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		
		doNetworking();
	}
	
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
		stage.addInGameActors();
		gameInputProcessor = new GameInputProcessor(localPlayer);
		inputMultiplexer.addProcessor(gameInputProcessor);
	}
	
	RemotePlayer addRemotePlayerToList(String playerName, int uid) {
		RemotePlayer remotePlayer = new RemotePlayer(playerShape, true);
		remotePlayer.uid = uid;
		remotePlayer.username = playerName;
		currentMap.players.add(remotePlayer);
		remotePlayer.setPos(new Point(-100, -100));
		return remotePlayer;
	}
}
