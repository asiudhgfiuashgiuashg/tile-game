package com.mygdx.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.net.*;

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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

public class TheGame extends ApplicationAdapter 
{
	LocalPlayer player;
	SpriteBatch batch;
	
	Map currentMap;
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
    
    private static enum GameState {
        MAIN_MENU,
        WAITING_FOR_START_SIGNAL,
        GAME_STARTED,
    }
    GameState gameState = GameState.MAIN_MENU;
    
	@Override
	public void create()
	{	
		batch = new SpriteBatch();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

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
		TextButtonStyle textButtonStyle = new TextButtonStyle();
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
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		labelStyle.fontColor = Color.WHITE;
		
		Label serverAddressLabel = new Label("server address: ", labelStyle);
	  
		final TextField serverAddressField = new TextField("", skin);
		serverAddressField.setWidth(200);
		serverAddressField.setHeight(30);
		serverAddressField.setAlignment(Align.center);
		
		
		Label serverPortLabel = new Label("port: ", labelStyle);
		
		final TextField serverPortField = new TextField("", skin);
		serverPortField.setWidth(70);
		serverPortField.setAlignment(Align.center);
		System.out.println(serverPortField.getWidth());
		serverPortField.setHeight(30);
		//only accept digits in port field
		serverPortField.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				if (Character.isDigit(c)) {
					return true;
				}
				return false;
			}
		});
		

		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton connectButton = new TextButton("Connect", skin);
		
		
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
		mainMenuTable.add(connectButton).colspan(4).center().padTop(40);
		//mainMenuTable.debugAll();
		

		// Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
		// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
		// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
		// revert the checked state.
		connectButton.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				if (null != errorTextField) {
					errorTextField.remove();
				}
				if (connectToServer(serverAddressField.getText(), Integer.parseInt(serverPortField.getText()))) {
					setupForInGame();
				}
			}
		});

		// Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
		//table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);
	}
	
	//attempts to connect to server, returns true for success
	private boolean connectToServer(String serverAddress, int port) {
		try {
			socket = new Socket(serverAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
		mainMenuTable.addActorAt(2, error);
		//error.debug();
	}
	
	/**
	 * create player and map
	 */
	private void setupForInGame() {
		Shape shape = new Shape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 55)),
				new LineSeg(new Point(15, 55), new Point(50, 55)),
				new LineSeg(new Point(50, 55), new Point(50, 0)),
				new LineSeg(new Point(50, 0), new Point(15, 0))
				),
				new Point(0,0));
		player = new LocalPlayer(shape, false);
		
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
            currentMap = new Map("../core/assets/" + mapName +".txt", "../core/assets/Tiles.txt", player);
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
		gameState = GameState.GAME_STARTED;
	}

	@Override
	public void render()
	{
		if (GameState.MAIN_MENU == gameState) {
			Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
			stage.draw();
		} else {
			//*******Networking*****
			//receiving during gameplay, after the game has started
			try {
				if (in.ready()) {
					//spin until receive message from server to start game (signaling that other client has connected, etc)
					if (GameState.GAME_STARTED != gameState) {
						JSONObject received = (JSONObject) JSONValue.parse(in.readLine());
						if (received.get("type").equals("gameStartSignal")) {
							gameState = GameState.GAME_STARTED; //start the game once the gameStartSignal is received from the server (signalling that the other client has connected, etc)
						}
						
						
					} else { //handle messages that come during game play, after the game has started
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
		        	float charX = (float) player.getPos().getX();
		        	float charY = (float) player.getPos().getY();
		        	obj.put("type", "position"); //let server know that this message specifies a position update
		            obj.put("charX", charX);
		            obj.put("charY", charY);
		        	out.println(obj.toString());
		        	
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
