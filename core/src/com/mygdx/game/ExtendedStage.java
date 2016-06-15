package com.mygdx.game;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.listeners.InLobbyMessageTextFieldListener;
import com.mygdx.game.listeners.InventoryButtonListener;
import com.mygdx.game.listeners.ItemListListener;
import com.mygdx.game.lobby.LobbyPlayer;
import com.mygdx.game.lobby.ui.LobbyPlayerTable;
import com.mygdx.game.player.Player;

/**
 * contains the GUI
 * @author elimonent
 *
 */
public class ExtendedStage extends Stage {
	
	/**
	 * used to communicate stage change events that the player makes to the server
	 * for example, the server should know any messages the player types into the chatbox
	 */
	private Communicator communicator;
	
	protected GameMap currentMap;
	protected Skin skin;
	public List<Item> itemList;
	
	private VerticalGroup chatMessagesVGroup;
    private static final int CHAT_BOX_HEIGHT = 80;
    private int numChatLines; //for in-lobby chat
    private TextField messageTextField;
    private InputListener inLobbyMessageTextFieldListener;
    private InputListener inGameMessageTextFieldListener;
	private Table lobbyTable;
	Map<LobbyPlayer, CheckBox> playerToCheckBoxMap;

	
	private Preferences preferences;
	private Table serverConnectTable;
	
	private static final Color GREEN = new Color(.168f, .431f, .039f, 1);
	private TextField errorTextField;
	
	private TheGame theGame;
	

	InventoryGroup inventoryGroup;
	
	public ExtendedStage(Skin skin, TheGame theGame, Communicator communicator) {
		this.skin = skin;
		numChatLines = 0;
		this.theGame = theGame;
		this.communicator = communicator;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (!super.keyDown(keycode)) { //if not already handled by some listener on the stage, take action or do nothing based on the keycode
			if (currentMap != null && Input.Keys.G == keycode) {
				ItemCollector items = currentMap.getNearbyItemList();
				if (null == itemList) {
					//view items on ground nearby
					itemList = new List<Item>(skin);
					itemList.setItems(items.itemList);
					itemList.setPosition((float) currentMap.localPlayer.getPos().getX(), (float) currentMap.localPlayer.getPos().getY());
					itemList.addListener(new ItemListListener(itemList, currentMap));
					
					itemList.debug();

					this.addActor(itemList);
					this.setKeyboardFocus(itemList);
					
				} else {
					itemList.remove();
					itemList = null;
				}
				
				return true; //handled
			}
		} else {
			return true;
		}
		return false;//not handled
	}

	public void updateItemList() {
		if (null != itemList) {
			itemList.setItems(currentMap.getNearbyItemList().itemList);
		}
	}
	
	public void setUpInGameGui() {
		
	}
	
	/**
	 * add a chat box to the stage
	 */
	public void addChatBox() {
		chatMessagesVGroup = new VerticalGroup();
		chatMessagesVGroup.debugAll();
		chatMessagesVGroup.setPosition(5, 35);
		chatMessagesVGroup.setSize(400, CHAT_BOX_HEIGHT);
		chatMessagesVGroup.left();
		chatMessagesVGroup.reverse();
		this.addActor(chatMessagesVGroup);
		
		
		messageTextField = new TextField("", skin);
		messageTextField.setSize(400, 30);

		
		messageTextField.setPosition(5, 5);
		this.addActor(messageTextField);
	}
	
	/**
	 * set up the ui elements and their listeners for the lobby
	 */
	public void setupLobby() {
		TheGame.gameState = TheGame.GameState.IN_LOBBY;
		playerToCheckBoxMap = new HashMap<LobbyPlayer, CheckBox>();
		//create local player
		//currentMap.players.add(player);
		
		final CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
		checkBoxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("art/checkbox_unchecked.png"))));
		checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("art/checkbox_checked.png"))));
		checkBoxStyle.font = skin.getFont("default");
		skin.add("default", checkBoxStyle);
		
		final CheckBox readyCheckBox = new CheckBox("", checkBoxStyle);
		//playerToCheckBoxMap.put(currentMap.localPlayer, readyCheckBox);
		readyCheckBox.setPosition(600, 70);
		//readyCheckBox.setWidth(100);
		//readyCheckBox.setHeight(30);
		//readyCheckBox.debug();
		//readyCheckBox.setSize(100, 50);

		
		readyCheckBox.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) { //notify server of readynes or unreadyness
				boolean ready = readyCheckBox.isChecked();
				communicator.setReady(ready);
			}
		});
		final Label readyCheckBoxLabel = new Label("Ready?", skin);
		readyCheckBoxLabel.setPosition(520,  70);
		

		


			

		
		
		this.clear();
		this.addActor(readyCheckBox);
		this.addActor(readyCheckBoxLabel);

		
		lobbyTable = new Table();
		lobbyTable.debugAll();
		lobbyTable.setFillParent(true);
		lobbyTable.setSize(200, 300);
		lobbyTable.center();
		this.addActor(lobbyTable);

		lobbyTable.row();
		
		addChatBox();
		/*
		 * save this listener so it can be removed later and replaced with a listener for in-game
		 */
		inLobbyMessageTextFieldListener = new InLobbyMessageTextFieldListener(messageTextField, communicator, this);
		messageTextField.addListener(inLobbyMessageTextFieldListener);

	}
	
	
	/** add player's info to lobby page
	 * 
	 * @param player
	 */
	public void addPlayerToLobbyStage(LobbyPlayer player) {
		LobbyPlayerTable playerTable = new LobbyPlayerTable(player, skin);
		
		
/*		final CheckBox readyCheckBox = new CheckBox("", skin);
		readyCheckBox.setDisabled(true);
		playerToCheckBoxMap.put(player, readyCheckBox);*/
		lobbyTable.add(playerTable);
		//lobbyTable.add(readyCheckBox);
		lobbyTable.row();
		///System.out.println("added player to lobby stage: " + player.username);
	}
	
	
	protected void setupConnectMenu() {
		
		this.clear();
		// A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
		// recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
		
		
		//http://www.vogella.com/tutorials/JavaPreferences/article.html
		preferences = Preferences.userRoot().node(this.getClass().getName()); //used to save/load fields on server connect page
		
		Label serverAddressLabel = new Label("Server Address: ", skin);
	  
		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton connectButton = new TextButton(" Connect ", skin);
				
		final TextField serverPortField = new TextField(preferences.get("serverPortField", ""), skin);
		serverPortField.setWidth(70);
		serverPortField.setAlignment(Align.center);
		///System.out.println(serverPortField.getWidth());
		serverPortField.setHeight(30);
		
		final TextField serverAddressField = new TextField(preferences.get("serverAddressField", ""), skin);
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
				
		Label serverPortLabel = new Label("Port: ", skin);
		
		
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
		
		final TextField usernameField = new TextField(preferences.get("username", ""), skin);
		usernameField.setWidth(200);
		usernameField.setHeight(30);
		usernameField.setAlignment(Align.center);
		
		Label usernameLabel = new Label("Username: ", skin);	
		
		TextButton backButton = new TextButton("Back", skin);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setupMainMenu();
			}
		});
		backButton.setPosition(700, 30);
		
		//create a table that fills the screen
		serverConnectTable = new Table();
		serverConnectTable.setFillParent(true);
		serverConnectTable.setSize(200, 300);
		serverConnectTable.center();
		this.addActor(serverConnectTable);
		
		//populate table
		serverConnectTable.add(serverAddressLabel);
		serverConnectTable.add(serverAddressField);
		serverConnectTable.add(serverPortLabel).padLeft(20);
		serverConnectTable.add(serverPortField).width(70);
		serverConnectTable.row();  //new row
		serverConnectTable.add(usernameLabel).padTop(20);
		serverConnectTable.add(usernameField).padTop(20);
		serverConnectTable.row();
		serverConnectTable.add(connectButton).colspan(4).center().padTop(40);
		//mainMenuTable.debugAll(); //show bounding boxes
		
		this.addActor(backButton);

		// Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
		// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
		// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
		// revert the checked state.
		connectButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (null != errorTextField) {
					errorTextField.remove();
				}
				if (serverAddressField.getText().length() > 0
						&& serverPortField.getText().length() > 0
						&& theGame.connectToServer(serverAddressField.getText(), Integer.parseInt(serverPortField.getText()), usernameField.getText())) {
					
					//save textFields for next game session
					preferences.put("username", usernameField.getText());
					preferences.put("serverPortField", serverPortField.getText());
					preferences.put("serverAddressField", serverAddressField.getText());

					
					theGame.setupLobby();
				}
			}
		});

		// Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
		//table.add(new Image(skin.newDrawable("white", Color.RED))).size(64);
	}
	
	protected void setupMainMenu() {
		this.clear();

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		final TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.over = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
		
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = skin.newDrawable("white", GREEN);
		textFieldStyle.font = skin.getFont("default");
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.cursor = skin.newDrawable("white", Color.WHITE);
		textFieldStyle.cursor.setMinWidth(2f);
		skin.add("default", textFieldStyle);
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont("default");
		labelStyle.fontColor = Color.WHITE;
		skin.add("default", labelStyle);
		
		TextButton joinServerButton = new TextButton("Join Server", skin);
		joinServerButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setupConnectMenu();
			}
			
		});
		
		joinServerButton.setPosition(this.getWidth() / 2 - joinServerButton.getWidth() / 2, this.getHeight() / 2);
		this.addActor(joinServerButton);
		
		TextButton hostServerButton = new TextButton("Host", skin);
		hostServerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setupServerHostScreen();
			}
		});
		
		hostServerButton.setPosition(this.getWidth() / 2 - hostServerButton.getWidth() / 2, 200);
		this.addActor(hostServerButton);
	}
	
	protected void setupServerHostScreen() {
		this.clear();
		Table table = new Table();
		preferences = Preferences.userRoot().node(this.getClass().getName());
		final TextField userNameTextField = new TextField(preferences.get("username", ""), skin);
		userNameTextField.setAlignment(Align.center);
		Label userNameLabel = new Label("Username: ", skin);
		table.add(userNameLabel);
		table.add(userNameTextField);
		table.row();
		table.setPosition(this.getWidth() / 2 - table.getWidth() / 2, this.getHeight() / 2);
		
		this.addActor(table);
		
		TextButton hostServerButton = new TextButton("Host", skin);
		hostServerButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				preferences.put("username", userNameTextField.getText()); //save the username for next time the game starts
				theGame.setupLobbyAsHost(userNameTextField.getText());
			}
		});
		
		hostServerButton.setPosition(this.getWidth() / 2 - hostServerButton.getWidth() / 2, table.getY() - 100);
		
		this.addActor(hostServerButton);
		
		TextButton backButton = new TextButton("Back", skin);
		backButton.setPosition(700, 30);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setupMainMenu();
			}
		});
		
		this.addActor(backButton);
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
	
	public void addMessageToChatbox(String message) {
		Label messageLabel = new Label(message, skin);
		while (messageLabel.getPrefWidth() > chatMessagesVGroup.getWidth()) {
			message = message.substring(0, message.length() - 1);
			messageLabel.setText(message);
		}
		chatMessagesVGroup.addActorAt(0, messageLabel);
		///System.out.println("added: " + messageLabel);
		numChatLines++;
		BitmapFont font = messageLabel.getStyle().font;
		int maxNumChatLines = (int) (CHAT_BOX_HEIGHT / (font.getCapHeight() + font.getAscent() + -font.getDescent()));
		if (maxNumChatLines == numChatLines) {
			numChatLines -= 1;
			chatMessagesVGroup.getChildren().get(chatMessagesVGroup.getChildren().size - 1).remove(); //get rid of top chat line
		}
	}

	public void displayError() {
		errorTextField = new TextField("could not connect", skin);
		errorTextField.setAlignment(Align.center);
		errorTextField.setDisabled(true); //so it can't be edited
		errorTextField.setPosition(0, 50);
		this.addActor(errorTextField);
	}
	
	/**
	 * add the ui elements for in-game
	 */
	public void addInGameActors() {
		addChatBox();
		messageTextField.setVisible(false);
		
		/**
		 * replace the chatbox listener from the lobby with a new one for in-game
		 */
		messageTextField.removeListener(inLobbyMessageTextFieldListener);
		inGameMessageTextFieldListener = new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					if (messageTextField.isVisible()) {
						//send the text as a message
						if (messageTextField.getText().length() > 0) {
							String message = messageTextField.getText();
							communicator.sendChatMessage(message);
							messageTextField.setText("");
						}
							
						messageTextField.setVisible(false); //close the text field
						messageTextField.setDisabled(true);
						event.setBubbles(false); //stop the event from bubbling back up to the stage, which will handle ENTER again (we only want ENTER to be handled once)
					}
					return true; // the event is "handled" -- no propogation outside of this stage
				}
				return false; // the event is not "handled" -- propogates outside of stage
			}
		};
		messageTextField.addListener(inGameMessageTextFieldListener);
		
		this.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER && !messageTextField.isVisible() && !(ExtendedStage.this.itemList != null)) {
					messageTextField.setVisible(true); //open up text field for message entry
					messageTextField.setDisabled(false);
					ExtendedStage.this.setKeyboardFocus(messageTextField);
					//currentMap.localPlayer.directionStack.clear();
					return true;
				}
				return false;
			}
		});
		
		
		for (Player player: currentMap.players) {
			if (player != this.currentMap.localPlayer) { //!= localPlayer
				LabelStyle labelStyle = new LabelStyle();
				labelStyle.font = skin.getFont("default");
				labelStyle.fontColor = Color.WHITE;
				Label playerNameLabel = new Label(player.username, labelStyle);
				/////System.out.println("position: " + player.getXPos());
				playerNameLabel.setPosition((float) player.getXPos(), (float) player.getYPos());
				(player).nameLabel = playerNameLabel;
				this.addActor(playerNameLabel);
			}
		}
		
		//setup horizontal portion of in-game gui
		Table horizontalGuiTable = new Table();
		horizontalGuiTable.padLeft(3);
		horizontalGuiTable.align(Align.topLeft);
		
		horizontalGuiTable.setPosition(chatMessagesVGroup.getX() + chatMessagesVGroup.getWidth(), chatMessagesVGroup.getHeight() + chatMessagesVGroup.getY());
		
		this.addActor(horizontalGuiTable);
	}
	
	
	public void openInventoryOverlay() {
		inventoryGroup = new InventoryGroup(skin, currentMap.localPlayer.inv.itemList);
		this.addActor(inventoryGroup);
	}

	public void setClass(String className) {
		
		
	}


	/**
	 * refresh a lobby player's label and stuff like that
	 * 
	 * call this when the lobby player's username or other displayed info has changed
	 * @param uid the uid of the player whose graphhical reprsentation in the lobby needs updated
	 */
	public void updateLobbyPlayerTable(int uid) {
		for (Actor child: lobbyTable.getChildren())  {
			LobbyPlayerTable playerTable = (LobbyPlayerTable) child;
			if (playerTable.getLobbyPlayer().getUid() == uid) {
				playerTable.refreshUsername();
			}
		}
	}
}
