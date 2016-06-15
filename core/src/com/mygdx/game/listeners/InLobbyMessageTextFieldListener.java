package com.mygdx.game.listeners;

import java.io.PrintWriter;

import org.json.simple.JSONObject;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.game.Communicator;
import com.mygdx.game.ExtendedStage;
import com.mygdx.game.player.Player;

public class InLobbyMessageTextFieldListener extends InputListener {
	private TextField messageTextField;
	private Communicator communicator;
	private Player localPlayer;
	private ExtendedStage stage;
	
	public InLobbyMessageTextFieldListener(TextField messageTextField, Communicator communicator, Player localPlayer, ExtendedStage stage) {
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
				stage.addMessageToChatbox(localPlayer.username + ": " + messageTextField.getText());
				messageTextField.setText("");
			}
			return true; //dont pass along the event
		}
		return false; //pass along the event
	}
}
