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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mygdx.ai.Agent;
import com.mygdx.ai.PositionIndexedNode;
import com.mygdx.ai.TestAi;
import com.mygdx.game.listeners.InLobbyMessageTextFieldListener;
import com.mygdx.game.listeners.InventoryButtonListener;
import com.mygdx.server.Server;
import com.mygdx.game.GameEventHandler;


public class TheGame extends ApplicationAdapter {
	SpriteBatch batch;
	
	public static GameMap currentMap;
	public static PrintWriter out;
    public static BufferedReader in;
    private long time;
    private final int SEND_SPACING = 50;
    private DirectionOfTravel playerOldDirection;
    private Skin skin;
    private ExtendedStage stage;
    private Socket socket;
    
    
    
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
    static GameState gameState;
    private InputMultiplexer inputMultiplexer; //will delegate events tos the game inputprocessor and the gui inputprocessor (the stage)
    private GameInputProcessor gameInputProcessor;
    
    private Server server;
    private boolean hosting;
    public static boolean debug;
    
    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 480;
    
	@Override
	public void create() {
		debug = true;
		
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
		stage = new ExtendedStage(skin, this); //the gui is laid out here
		stage.setDebugAll(true);
		stage.skin = skin;
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		
		Gdx.input.setInputProcessor(inputMultiplexer); //the stage which contains the gui/hud gets to handle inputs first, and then pass the ones it doesn't handle down to the game
		
		playerShape = new Shape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 35)),
				new LineSeg(new Point(15, 35), new Point(50, 35)),
				new LineSeg(new Point(50, 35), new Point(50, 0)),
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
			GameEvent.handler();
			
			socket = new Socket(serverAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			stage.out = out;
			
			
			localPlayer = new LocalPlayer(playerShape, false);
			localPlayer.username = username;
			setupForInGame();
			
			
			
			//send server player info, such as username
			JSONObject outObj = new JSONObject();
			outObj.put("type", "playerInfo");
			outObj.put("username", localPlayer.username);
			out.println(outObj);
			return true;
			
		} catch (Exception e) {
			stage.displayError();
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * create player and map
	 */
	private void setupForInGame() {
		batch = new SpriteBatch();
		
		
		Scanner sc = new Scanner(System.in);
        ///System.out.println("Which map would you like to test?");
        String mapName = "1Square";//sc.nextLine();
        
        sc.close();
        
		try {
            ///System.out.println("Working Directory = " + System.getProperty("user.dir"));
            currentMap = new GameMap("../core/assets/" + mapName +".txt", "../core/assets/Tiles.txt", localPlayer);
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
            System.out.println(e.getMessage());
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
			
			currentMap.draw(batch);
			currentMap.update(batch);
			
			
			batch.end();
			
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
		//*******Networking*****
		if (GameState.SERVER_CONNECT_SCREEN != gameState) {
			try {
				if (in.ready()) {
					String receivedStr = in.readLine();
					JSONObject received = (JSONObject) JSONValue.parse(receivedStr);
					Gdx.app.log(getClass().getSimpleName(), "received: " + receivedStr);
					if (received.get("type").equals("chatMessage")) {
						String message = (String) received.get("message");
						System.out.println("message: " + message);
						stage.addMessageToChatbox(message);
					}
					
					
					//spin until receive message from server to start game (signaling that other client has connected, etc)
					if (GameState.IN_LOBBY == gameState) {

						if (received.get("type").equals("gameStartSignal")) {
							gameState = GameState.GAME_STARTED;
							stage.clear();
							addInGameActors();
							
						} else if (received.get("type").equals("playerInfo")) {
							String playerName = (String) received.get("username");
							int uid = ((Number) received.get("uid")).intValue();
							RemotePlayer remotePlayer = addRemotePlayerToList(playerName, uid);
							/////System.out.println("remotePlayer info received: " + remotePlayer == null);
							stage.addPlayerToLobbyStage(remotePlayer);
							System.out.println("added remotePlayer: " + remotePlayer);
							
						} else if (received.get("type").equals("readyStatus")) {
							int uid = ((Number) received.get("uid")).intValue();
							boolean isReady = (Boolean) received.get("readyStatus");
							for (Player player: stage.playerToCheckBoxMap.keySet()) {
								if (player.uid == uid) {
									stage.playerToCheckBoxMap.get(player).setChecked(isReady);
								}
							}
							
						} else if (received.get("type").equals("uidUpdate")) {
							localPlayer.uid = ((Number) received.get("uid")).intValue();
							
						} else if (received.get("type").equals("sprite")) { //updates sprites for remoteplayers
		        			int uid = ((Number) received.get("uid")).intValue();
		        			(currentMap.getPlayerByUid(uid)).changeAppearance((String) received.get("spriteID"));
		        			
		        		}
						
					} else if (GameState.GAME_STARTED == gameState) { //handle messages that come during game play, after the game has started
		        		String messageType = (String) received.get("type");
		        		//position updates
		        		if (messageType.equals("position")) {
			        		double otherPlayerX = ((Number) received.get("charX")).floatValue();
			        		double otherPlayerY = ((Number) received.get("charY")).floatValue();
			        		int uid = ((Number) received.get("uid")).intValue();
			        		currentMap.getPlayerByUid(uid).setPos(new Point(otherPlayerX, otherPlayerY));
			        		
		        		} else if (messageType.equals("animation")) { //animation updates
		        			//System.out.println("animation: " + received);
		        			int uid = ((Number) received.get("uid")).intValue();
		        			((RemotePlayer) currentMap.getPlayerByUid(uid)).setAnimation((String) received.get("animationName"));
		        			
		        		} else if (messageType.equals("removedItem")) { //item removed from ground
		        			int uid = ((Number) received.get("uid")).intValue(); //unique identifier of item which was removed
		        			currentMap.itemsOnField.removeByUid(uid);
		        			stage.updateItemList(); //repopulate itemList to get rid of the listing for the removed item
		        			
		        		} else if (messageType.equals("inventoryAddition")) { //arrives before the removedItem message
		        			int uid = ((Number) received.get("uid")).intValue();
		        			localPlayer.inv.addItem(currentMap.itemsOnField.getByUid(uid));
		        			
		        		} else if (messageType.equals("itemDrop")) {
		        			
		        			Item droppedItem = new Item(received); //create an item from the json
		        			Gdx.app.log(getClass().getSimpleName(), "dropped item: " + droppedItem);
		        			currentMap.getItemList().itemList.add(droppedItem);
		        		}
		                
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//sending messagse to server
			//TODO maybe need a more advanced queue so we arent sending every message type at the same time
			// maybe not, maybe tcp already handles queues of messages pretty well
			if (GameState.GAME_STARTED == gameState) {
		    	JSONObject obj = new JSONObject();
		        if (System.currentTimeMillis() - time >= SEND_SPACING) {
		        	//sending position
		        	obj.clear();
		        	if (!localPlayer.getPos().equals(oldPos)) { //don't send unnecessary updates
			        	float charX = (float) localPlayer.getPos().getX();
			        	float charY = (float) localPlayer.getPos().getY();
			        	obj.put("type", "position"); //let server know that this message specifies a position update
			            obj.put("charX", charX);
			            obj.put("charY", charY);
			            obj.put("uid", localPlayer.uid);
			        	out.println(obj.toString());
			        	oldPos = localPlayer.getPos();
		        	}
		        	//sending direction
		        	if (localPlayer.direction != playerOldDirection) {

		        		//System.out.println(localPlayer.direction.toString());

			        	obj.clear();
			        	obj.put("type", "direction");
			        	obj.put("direction", localPlayer.direction.toString());
			        	out.println(obj.toString());
		        	}
		        	playerOldDirection = localPlayer.direction;
		        	
		        	//update time
		        	time = System.currentTimeMillis();
		        }
			}
		}
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
	
	private RemotePlayer addRemotePlayerToList(String playerName, int uid) {
		RemotePlayer remotePlayer = new RemotePlayer(playerShape, true);
		remotePlayer.uid = uid;
		remotePlayer.username = playerName;
		currentMap.players.add(remotePlayer);
		remotePlayer.setPos(new Point(-100, -100));
		return remotePlayer;
	}
}
