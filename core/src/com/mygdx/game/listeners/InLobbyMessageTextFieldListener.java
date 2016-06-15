package com.mygdx.game.listeners;

import java.io.PrintWriter;

import org.json.simple.JSONObject;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.game.Communicator;
import com.mygdx.game.ExtendedStage;
import com.mygdx.game.lobby.LobbyPlayer;
import com.mygdx.game.player.Player;

/**
 * this listener will listen for the enter key to be pressed in the lobby chat field
 *  and then send the message to the server
 * @author elimonent
 *
 */
public class InLobbyMessageTextFieldListener extends InputListener {
	private TextField messageTextField;
	private Communicator communicator;
	private LobbyPlayer localPlayer;
	private ExtendedStage stage;
	
	public InLobbyMessageTextFieldListener(TextField messageTextField, Communicator communicator, ExtendedStage stage) {
		this.communicator = communicator;
		this.messageTextField = messageTextField;
		this.localPlayer = localPlayer;
		this.stage = stage;
	}
	
	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		if (keycode == Input.Keys.ENTER) {
			if (messageTextField.getText().length() > 0) {
				String message =  messageTextField.getText();
				communicator.sendChatMessage(message);
				messageTextField.setText("");
			}
			return true; //dont pass along the ENTER key event, since ENTER is used to activate the chatbox
		}
		return false; //pass along the event if it isn't the ENTER key
	}
}
