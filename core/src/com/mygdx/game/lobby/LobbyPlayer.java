package com.mygdx.game.lobby;

/**
 * represents a player in a lobby
 * 
 * upon transition from lobby to in-game, the lobbyplayers should turn into Players on the GameMap
 * @author elimonent
 *
 */
public class LobbyPlayer {
	
	
	/**
	 * the currently selected class of this lobby client
	 */
	private CurrentClass currentClass;
	
	/**
	 * the unique identifier of this player (used by the server to refer to this player).
	 * upon transition from lobby to in-game, this uid should be preserved in an instance of Player
	 */
	private int uid;
	
	/**
	 * the username of this player (not necessarily unique)
	 */
	private String username;
	
	public LobbyPlayer(String username, int uid) {
		setUid(uid);
		setUsername(username);
	}
	
	private void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * 
	 * @return the class that this player has selected
	 */
	public CurrentClass getCurrentClass() {
		return currentClass;
	}
	
	/**
	 * set the class that this lobby player has selected (call this in response to a network message)
	 * @param newClass the class to set currentClass to
	 */
	protected void setCurrentClass(CurrentClass newClass) {
		this.currentClass = newClass;
		//TODO gui stuff
	}

	public String getUsername() {
		return username;
	}

	/**
	 * perhaps check for duplicate usernames in the lobby here and put a (1) after a duplicate
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public int getUid() {
		return uid;
	}
	
	@Override
	public String toString() {
		return currentClass
				+ "\nusername: " + username
				+ "\nuid: " + getUid();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof LobbyPlayer)) {
			return false;
		}
		LobbyPlayer theOther = (LobbyPlayer) other;
		return this.uid == theOther.uid;
	}
	
	@Override
	public int hashCode() {
		return this.uid;
	}
}
