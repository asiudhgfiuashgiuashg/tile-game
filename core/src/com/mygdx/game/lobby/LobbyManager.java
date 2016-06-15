package com.mygdx.game.lobby;

import java.util.*;

import com.badlogic.gdx.Gdx;

/**
 * a class for helping manage lobbies (the screen before the game starts where all the clients connect)
 * 
 * upon transition from lobby to in game, thhe lobbyPlayers will be converted into Players (reprseentations of players on the game map)
 * @author elimonent
 *
 */
public class LobbyManager {
	/**
	 * a list of players added up in the lobby
	 */
	private List<LobbyPlayer> lobbyPlayers;
	
	/**
	 * the local player
	 */
	private LobbyPlayer localLobbyPlayer;
	
	public LobbyManager() {
		lobbyPlayers = new ArrayList<LobbyPlayer>();
	}

	public LobbyPlayer getLocalLobbyPlayer() {
		return localLobbyPlayer;
	}

	/**
	 * set the local lobby player, remove the old one if need be, and put the new one in the list of lobby players
	 * @param localLobbyPlayer
	 */
	public void setLocalLobbyPlayer(LobbyPlayer localLobbyPlayer) {
		if (this.localLobbyPlayer != null) {
			lobbyPlayers.remove(this.localLobbyPlayer);
		}
		lobbyPlayers.add(localLobbyPlayer);
		this.localLobbyPlayer = localLobbyPlayer;
	}

	public List<LobbyPlayer> getLobbyPlayers() {
		return lobbyPlayers;
	}

	/**
	 * 
	 * @param uid the uid of the player to change the class of
	 * @param className thhe name of the class to set the player with this UID to
	 */
	public void setClassByUid(int uid, CurrentClass theClass) {
		Gdx.app.log(this.getClass().getSimpleName(), "setting class by uid");
		for (LobbyPlayer player: getLobbyPlayers()) {
			Gdx.app.log(this.getClass().getSimpleName(), player.toString());
		}
		
		LobbyPlayer lobbyPlayer = null;
		lobbyPlayer = getLobbyPlayerByUid(uid);

		
		lobbyPlayer.setCurrentClass(theClass);
	}

	private LobbyPlayer getLobbyPlayerByUid(int uid) {
		/*
		 * find the lobby player with this uid
		 */
		int index = 1;
		LobbyPlayer lobbyPlayer = lobbyPlayers.get(0);
		while (lobbyPlayer.getUid() != uid) {
			lobbyPlayer = lobbyPlayers.get(index);
			index++;
		}

		return lobbyPlayer;
	}

	public void setUsernameByUid(String username, int uid) {
		LobbyPlayer lobbyPlayer = getLobbyPlayerByUid(uid);
		lobbyPlayer.setUsername(username);
	}


}
