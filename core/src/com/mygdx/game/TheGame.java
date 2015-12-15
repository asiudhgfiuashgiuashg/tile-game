package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TheGame extends ApplicationAdapter 
{
	LocalPlayer player;
	SpriteBatch batch;
	
	GameMap currentMap;
	GuiManager mapGuiManager; //holds all gui elements which are displayed when map is visible
	GuiManager mainMenuGuiManager;
	//GuiManager mainMenuGuiManager
	//etc
	boolean itemListExists;
	PrintWriter out;
    BufferedReader in;
    long time;
    final int SEND_SPACING = 50;
    Direction playerOldDirection;
    Skin skin;
    Stage stage;
    Socket socket;
    Table mainMenuTable;
    TextField errorTextField;
    List<Player> players;
    List<Player> playersDrawnInLobby;
    LabelStyle labelStyle;
    Table lobbyTable;
    Shape playerShape;
    Point oldPos;
    Map<Player, CheckBox> playerToCheckBoxMap;
    
    private static enum GameState {
        MAIN_MENU,
        IN_LOBBY,
        WAITING_FOR_START_SIGNAL,
        GAME_STARTED,
    }
    GameState gameState;
    
	@Override
	public void create() {
		oldPos = new Point(0, 0);
		gameState = GameState.MAIN_MENU;
		batch = new SpriteBatch();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
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
		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
		skin = new Skin();

		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		skin.add("default", new BitmapFont());

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		final TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.over = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
		
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = skin.newDrawable("white", Color.OLIVE);
		textFieldStyle.font = skin.getFont("default");
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = skin.newDrawable("white", Color.WHITE);
		textFieldStyle.cursor.setMinWidth(2f);
		skin.add("default", textFieldStyle);
		
		labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		labelStyle.fontColor = Color.WHITE;
		skin.add("default", labelStyle);
		
		Label serverAddressLabel = new Label("Server Address: ", labelStyle);
	  
		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton connectButton = new TextButton("Connect", skin);
				
		final TextField serverPortField = new TextField("", skin);
		serverPortField.setWidth(70);
		serverPortField.setAlignment(Align.center);
		System.out.println(serverPortField.getWidth());
		serverPortField.setHeight(30);
		
		final TextField serverAddressField = new TextField("", skin);
		serverAddressField.setWidth(200);
		serverAddressField.setHeight(30);
		serverAddressField.setAlignment(Align.center);
		serverAddressField.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				if ('.' == c || Character.isDigit(c) || Character.isAlphabetic(c)) {
					if (serverPortField.getText().length() > 0) { //highlight and enable connect button
						setEnabledAndHighlight(connectButton, true);
					}
					return true;
				}
				if (textField.getText().length() == 0) {
					setEnabledAndHighlight(connectButton, false);
				}
				return false;
			}
			
		});
		
		;
				
		Label serverPortLabel = new Label("Port: ", labelStyle);
		
		
		//only accept digits in port field
		serverPortField.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				if (Character.isDigit(c)) {
					if (serverAddressField.getText().length() > 0) { //highlight and enable connect button
						setEnabledAndHighlight(connectButton, true);
					}
					return true;
				}
				if (textField.getText().length() == 0) {
					setEnabledAndHighlight(connectButton, false);
				}
				return false;
			}
		});
		
		final TextField usernameField = new TextField("", skin);
		usernameField.setWidth(200);
		usernameField.setHeight(30);
		usernameField.setAlignment(Align.center);
		
		Label usernameLabel = new Label("Username: ", labelStyle);	

		
		//create a table that fills the screen
		mainMenuTable = new Table();
		mainMenuTable.setFillParent(true);
		mainMenuTable.setSize(200, 300);
		mainMenuTable.center();
		stage.addActor(mainMenuTable);
		
		//populate table
		mainMenuTable.add(serverAddressLabel);
		mainMenuTable.add(serverAddressField);
		mainMenuTable.add(serverPortLabel).padLeft(20);
		mainMenuTable.add(serverPortField).width(70);
		mainMenuTable.row();  //new row
		mainMenuTable.add(usernameLabel).padTop(20);
		mainMenuTable.add(usernameField).padTop(20);
		mainMenuTable.row();
		mainMenuTable.add(connectButton).colspan(4).center().padTop(40);
		//mainMenuTable.debugAll(); //show bounding boxes
		

		// Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
		// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
		// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
		// revert the checked state.
		connectButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				if (null != errorTextField) {
					errorTextField.remove();
				}
				if (serverAddressField.getText().length() > 0
						&& serverPortField.getText().length() > 0
						&& connectToServer(serverAddressField.getText(), Integer.parseInt(serverPortField.getText()), usernameField.getText())) {
					
					setupLobby();
					//setupForInGame();
				}
			}
		});

		// Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
		//table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);
	}
	private void setupLobby() {
		playerToCheckBoxMap = new HashMap<Player, CheckBox>();
		players = new ArrayList<Player>();
		//create local player
		players.add(player);
		
		gameState = GameState.IN_LOBBY;
		
		final CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
		checkBoxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("art/checkbox_unchecked.png"))));
		checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("art/checkbox_checked.png"))));
		checkBoxStyle.font = skin.getFont("default");
		skin.add("default", checkBoxStyle);
		
		final CheckBox readyCheckBox = new CheckBox("", checkBoxStyle);
		playerToCheckBoxMap.put(player, readyCheckBox);
		readyCheckBox.setPosition(600, 70);
		//readyCheckBox.setWidth(100);
		//readyCheckBox.setHeight(30);
		//readyCheckBox.debug();
		//readyCheckBox.setSize(100, 50);
		
		readyCheckBox.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) { //notify server of readynes or unreadyness
				JSONObject readyMessage = new JSONObject();
				readyMessage.put("type", "readyStatus");
				readyMessage.put("readyStatus", readyCheckBox.isChecked());
				out.println(readyMessage);
				
				//check the box by name as well
				playerToCheckBoxMap.get(player).setChecked(readyCheckBox.isChecked());
			}
		});
		final Label readyCheckBoxLabel = new Label("Ready?", skin);
		readyCheckBoxLabel.setPosition(520,  70);
		
		stage.clear();
		stage.addActor(readyCheckBox);
		stage.addActor(readyCheckBoxLabel);
		lobbyTable = new Table();
		lobbyTable.debugAll();
		lobbyTable.setFillParent(true);
		lobbyTable.setSize(200, 300);
		lobbyTable.center();
		stage.addActor(lobbyTable);
		addPlayerToLobbyStage(player);
		lobbyTable.row();
	}
	private void setEnabledAndHighlight(Button button, boolean enabled) {
		Button.ButtonStyle buttonStyle = button.getStyle();
		if (enabled) { //highlight connect button
			buttonStyle.up = skin.newDrawable("white", Color.LIGHT_GRAY);
			buttonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
			buttonStyle.checked = skin.newDrawable("white", Color.LIGHT_GRAY);
			buttonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		} else {
			buttonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
			buttonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
			buttonStyle.checked = skin.newDrawable("white", Color.DARK_GRAY);
			buttonStyle.over = skin.newDrawable("white", Color.DARK_GRAY);
		}
		button.setDisabled(!enabled);
	}
	//attempts to connect to server, returns true for success
	private boolean connectToServer(String serverAddress, int port, String username) {
		try {
			socket = new Socket(serverAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			player = new LocalPlayer(playerShape, false);
			player.username = username;
			
			setupForInGame();
			//send server player info, such as username
			JSONObject outObj = new JSONObject();
			outObj.put("type", "playerInfo");
			outObj.put("username", player.username);
			out.println(outObj);
			return true;
			
		} catch (Exception e) {
			errorTextField = new TextField("could not connect", skin);
			errorTextField.setAlignment(Align.center);
			displayConnectError(errorTextField);
			e.printStackTrace();
			return false;
		}
	}
	
	private void displayConnectError(TextField error) {
		error.setDisabled(true); //so it can't be edited
		mainMenuTable.addActorAt(2, error);
		//error.debug();
	}
	
	/**
	 * create player and map
	 */
	private void setupForInGame() {
		batch = new SpriteBatch();
		
		//player.create(); responsibilities for create() moved to constructor
		player.setFOV(player.sightX, player.sightY);
		
		Scanner sc = new Scanner(System.in);
        System.out.println("Which map would you like to test?");
        String mapName = "1Square";//sc.nextLine();
        
        sc.close();
        
		try
        {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            currentMap = new GameMap("../core/assets/" + mapName +".txt", "../core/assets/Tiles.txt", player);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to create map object");
        }
		
		//initialize various GuiManagers, giving them appropriate GuiElements
		mapGuiManager = new GuiManager();
		mainMenuGuiManager = new GuiManager();
		GuiManager.setCurrentManager(mapGuiManager);
		itemListExists = false;
		
		
		time = System.currentTimeMillis();
	}

	@Override
	public void render()
	{
		if (GameState.MAIN_MENU == gameState || GameState.IN_LOBBY == gameState) {
			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
			stage.draw();
			
			if (GameState.IN_LOBBY == gameState) {
				/*for(Player player: players) {
					if (!playersDrawnInLobby.contains(player)) {
						addPlayerToLobbyStage(player);
						playersDrawnInLobby.add(player);
					}
				}*/
			}
			
		} else {
			//System.out.println(player.getShape());
			/*
			 * logic for switching between various GuiManagers could go here
			 * if (character.health <= 0) {
			 * 		GuiManager.setCurrentGuiManager(mainMenuGuiManager);
			 * } else if (...
			 */
			keyListening();
			
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			
			if(GuiManager.currentManager.equals(mapGuiManager))
			{
				currentMap.draw(batch);
				currentMap.update(batch);
			}
			GuiManager.currentManager.draw(batch);
			GuiManager.currentManager.update();
			
			batch.end();
		}
		
		//*******Networking*****
		//receiving during gameplay, after the game has started
		if (GameState.MAIN_MENU != gameState) { 
			try {
				if (in.ready()) {
					//spin until receive message from server to start game (signaling that other client has connected, etc)
					if (GameState.IN_LOBBY == gameState) {
						System.out.println("ready");
						
						String receivedStr = in.readLine();
						System.out.println("receivedStr: " + receivedStr);
						JSONObject received = (JSONObject) JSONValue.parse(receivedStr);
						System.out.println("received: " + received);
						
						if (received.get("type").equals("gameStartSignal")) {
							gameState = GameState.GAME_STARTED;
							
						} else if (received.get("type").equals("playerInfo")) {
							String playerName = (String) received.get("username");
							//System.out.println("playername: " + playerName);
							RemotePlayer remotePlayer = addRemotePlayerToList(playerName);
							//System.out.println("remotePlayer info received: " + remotePlayer == null);
							addPlayerToLobbyStage(remotePlayer);
							lobbyTable.row();
							
						} else if (received.get("type").equals("readyStatus")) {
							String username = (String) received.get("username");
							boolean isReady = (Boolean) received.get("readyStatus");
							for (Player player: playerToCheckBoxMap.keySet()) {
								if (player.username.equals(username)) {
									playerToCheckBoxMap.get(player).setChecked(isReady);
								}
							}
						}
						
					} else if (GameState.GAME_STARTED == gameState) { //handle messages that come during game play, after the game has started
		        		String inputLine = in.readLine();
		        		JSONObject received = (JSONObject) JSONValue.parse(inputLine);
		        		String messageType = (String) received.get("type");
		        		//position updates
		        		if (messageType.equals("position")) {
			        		double secondPlayerX = ((Number) received.get("charX")).floatValue();
			        		double secondPlayerY = ((Number) received.get("charY")).floatValue();
			        		currentMap.player2.setPos(new Point(secondPlayerX, secondPlayerY));
		        		} else if (messageType.equals("animation")) { //animation updates
		        			currentMap.player2.setAnimation((String) received.get("animationName"));
		        			System.out.println("received animation message: " + received);
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
		        	if (!player.getPos().equals(oldPos)) { //don't send unnecessary updates
			        	float charX = (float) player.getPos().getX();
			        	float charY = (float) player.getPos().getY();
			        	obj.put("type", "position"); //let server know that this message specifies a position update
			            obj.put("charX", charX);
			            obj.put("charY", charY);
			        	out.println(obj.toString());
			        	oldPos = player.getPos();
		        	}
		        	//sending direction
		        	//note -- if not moving, all of these bools will be false
		        	if (currentMap.player.direction != playerOldDirection) {
			        	obj.clear();
			        	obj.put("type", "direction");
			        	obj.put("isMovingLeft", currentMap.player.isMovingLeft);
			        	obj.put("isMovingRight", currentMap.player.isMovingRight);
			        	obj.put("isMovingDown", currentMap.player.isMovingDown);
			        	obj.put("isMovingUp", currentMap.player.isMovingUp);
			        	out.println(obj.toString());
		        	}
		        	playerOldDirection = currentMap.player.direction;
		        	
		        	//update time
		        	time = System.currentTimeMillis();
		        }
			}
			//**end networking******
		}
	}
	public RemotePlayer addRemotePlayerToList(String playerName) {
		RemotePlayer remotePlayer = new RemotePlayer(playerShape, true);
		remotePlayer.username = playerName;
		players.add(remotePlayer);
		
		return remotePlayer;
	}
	/** add player's info to lobby page**/
	public void addPlayerToLobbyStage(Player player) {
		Label playerNameLabel = new Label(player.username, labelStyle);
		final CheckBox readyCheckBox = new CheckBox("", skin);
		readyCheckBox.setDisabled(true);
		playerToCheckBoxMap.put(player, readyCheckBox);
		lobbyTable.add(playerNameLabel).padTop(15).padRight(20);
		lobbyTable.add(readyCheckBox);
		System.out.println("added player to lobby stage: " + player.username);
	}
	
	public void keyListening() {
		
		if (GuiManager.currentManager.equals(mapGuiManager)) 
		{
			if (Gdx.input.isKeyJustPressed(Keys.G)) {
				if (!itemListExists && !currentMap.getNearbyItemList().isEmpty()) {
					ItemCollector items = currentMap.getNearbyItemList();
					GuiItemList guiItemList = new GuiItemList(currentMap.player, 0);
					guiItemList.setItemList(items);
					
					GuiManager.currentManager.addElement(guiItemList);
					GuiManager.currentManager.setFocused(guiItemList);
					itemListExists = true;
					player.setCanMove(false);
					GuiManager.currentManager.listen();
					
					guiItemList.watchedList = currentMap.itemsOnField;
				
					
					
				} else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
					player.setCanMove(false);
					TextInputProcessor chatInputProcessor = new TextInputProcessor();
					Gdx.input.setInputProcessor(chatInputProcessor);
				} else {
					GuiManager.currentManager.clearElements();
					itemListExists = false;
					player.setCanMove(true);
				}
				
			}
			if (Gdx.input.isKeyJustPressed(Keys.C))
			{	
				GuiManager.currentManager = mainMenuGuiManager;
			}
		}
		else if (GuiManager.currentManager.equals(mainMenuGuiManager))
			{
				if (Gdx.input.isKeyJustPressed(Keys.C))
				{	
					GuiManager.currentManager = mapGuiManager;
				}
				
			}
/*		} else if (currentManager.equals(mainMenuGuiManager) {
			some other behavior
		} else if (currentManager.equals(someOtherGuiManager) {
			some other behavior
		}
		.
		.
		.
*/
		
		
	}
}
