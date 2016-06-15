package com.mygdx.game.lobby.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.mygdx.game.lobby.LobbyPlayer;

/**
 * a ui element to represent a player in the lobby.
 * Minimal at the moment, but will be expanded in the future
 * @author elimonent
 *
 */
public class LobbyPlayerTable extends Table {
	/**
	 * displays the player's username
	 */
	private Label playerNameLabel;
	/**
	 * the skin which contains ui resources we will need
	 */
	private Skin skin;
	/**
	 * the lobby player who this table represents
	 */
	private LobbyPlayer lobbyPlayer;
	

	/**
	 * 
	 * @param player the player in the lobby that this table will represent graphically
	 */
	public LobbyPlayerTable(LobbyPlayer lobbyPlayer, Skin skin) {
		this.skin = skin;
		this.lobbyPlayer = lobbyPlayer;
		playerNameLabel = new Label(lobbyPlayer.getUsername(), skin.get("default", LabelStyle.class));
		add(playerNameLabel);
	}
	
	/**
	 * update the username (need this because the username isn't received immediately when a player connects)
	 * 
	 * eventually other ui elements like those related to the player's chosen class will need to have refresh methods as well
	 * 
	 * Call this after updating the lobbyPlayer's username field.
	 */
	public void refreshUsername() {
		playerNameLabel.setText(lobbyPlayer.getUsername());
	}
	
	public LobbyPlayer getLobbyPlayer() {
		return lobbyPlayer;
	}

}
