package com.mygdx.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.TheGame.GameState;
import com.mygdx.game.lobby.LobbyPlayer;
import com.mygdx.game.player.*;
import com.mygdx.game.lobby.CurrentClass;

/**
 * the purpose of this class is to translate from the protocol used to communicate over the network to model
 *  and translate model changes to the network protocol. By network protocol I don't mean the network part of the stack,
 *  I mean JSON messages sent over TCP
 *  
 * Changes in the local player will result be translated into network messages by this class
 * 
 * Network messages about changes in the game's model on the server side will be translated into local model changes by this class
 * @author elimonent
 *
 */
public class Communicator {
	/**
	 * the socket which will be used to establish the TCP stream
	 */
	private Socket socket;
	/**
	 * write messages to this field to have them sent to the server
	 */
	private PrintWriter out;
	/**
	 * messages from the server can be read from this field
	 */
	private BufferedReader in;
	
	/**
	 * the communicator must know the stage which lays out the GUI because network events may affect the GUI
	 */
	private ExtendedStage stage;
	
	/**
	 * Some messages from the server may modify the game's state.
	 * TheGame keeps track of the game's general state,
	 * and so the communicator must have a reference to theGame in order to let it know about these messages
	 */
	private TheGame theGame;
	
	/*
	 * variables used to avoid flooding the server with position updates
	 * 
	 * in the future, more complicated/involved rate limited will be nice
	 */
	private long timeLastSentPosition;
	/*
	 * variables used to avoid flooding the server with directioon updates
	 * 
	 * in the future, more complicated/involved rate limited will be nice
	 */
	private long timeLastSentDirection;
	/*
	 * max amt of time between position and direction updates
	 */
	private static final int MAX_POSITION_AND_DIRECTION_UPDATES_SENT_PER_SECOND = 20;
	
	
	public Communicator(TheGame theGame) {
		this.theGame = theGame;
	}

	/**
	 * connects to a server. After calling this function, the communicator's communication will all be between it and this server
	 * @param serverAddress server IP address
	 * @param port server port
	 * @param username the username to connect with
	 * @return whether the connection was successful
	 */
	public boolean connectToServer(InetAddress serverAddress, int port, String username) {
		try {
			socket = new Socket(serverAddress, port);
			socket.setTcpNoDelay(true); // turn off nagle's algorithm which will stop the tcp portion of the stack from buffering the server's messages and then delivering them all at once
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//send server player info, such as username
			JSONObject outObj = new JSONObject();
			outObj.put("type", "playerInfo");
			outObj.put("username", username);
			out.println(outObj);
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * send a message to appear in the chat box to the other players
	 * @param message the message that will appear in the chat next to this player's name
	 * @return whether the message was delivered successfully
	 */
	public boolean sendChatMessage(String message) {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("type", "chatMessage");
		jsonMessage.put("message", message);
		out.println(jsonMessage);
		
		return true;
	}
	
	
	/**
	 * When players are waiting in the lobby/party setup page for the game to start, they can choose to be ready to start or unready
	 * This function lets the server know a player's ready status
	 * @param ready
	 * @return true if successfully sent status to server
	 */
	public boolean setReady(boolean ready) {
		JSONObject readyMessage = new JSONObject();
		readyMessage.put("type", "readyStatus");
		readyMessage.put("readyStatus", ready);
		out.println(readyMessage);
		
		return true;
	}
	
	/**
	 * used by the localplayer class to send updates about their position when it changes (aka when the user moves their avatar)
	 * @param pos the new position of the player
	 */
	public void sendLocalPlayerPosition() {
		if (canSendPosition()) {
			/*
			 * construct the message
			 */
			JSONObject message = new JSONObject();
			double charX = TheGame.currentMap.localPlayer.getXPos();
	    	double charY = TheGame.currentMap.localPlayer.getYPos();
	    	message.put("type", "position"); //let server know that this message specifies a position update
	        message.put("charX", charX);
	        message.put("charY", charY);
	        message.put("uid", TheGame.currentMap.localPlayer.uid);
	        /*
	         * send the message
	         */
	    	out.println(message.toString());
		}
	}
	
	/**
	 * used to check if the position can be sent or if it is too soon
	 * used to avoid flooding the server with position messages
	 * @return true if can send 
	 */
	private boolean canSendPosition() {
		int oneSecond = 1000; //1000 ms
		int delayBetweenMsgs = oneSecond / MAX_POSITION_AND_DIRECTION_UPDATES_SENT_PER_SECOND;
		if (System.currentTimeMillis() - timeLastSentPosition >= delayBetweenMsgs) {
			timeLastSentPosition = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	/**
	 * used to check if the direction can be sent or if it is too soon
	 * used to avoid flooding the server with position messages
	 * @return true if can send 
	 */
	private boolean canSendDirection() {
		int oneSecond = 1000; //1000 ms
		int delayBetweenMsgs = oneSecond / MAX_POSITION_AND_DIRECTION_UPDATES_SENT_PER_SECOND;
		if (System.currentTimeMillis() - timeLastSentDirection >= delayBetweenMsgs) {
			timeLastSentDirection = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	
	/**
	 * send the local player's direction to the server
	 * aka translate the local representation of the player's direction into a network message
	 */
	public void sendLocalPlayerDirection() {
		if (canSendDirection()) {
			JSONObject message = new JSONObject();
			/*
			 * construct the message 
			 */
			message.put("type", "direction");
	    	message.put("direction", TheGame.currentMap.localPlayer.getDirection().toString());
	    	/*
	    	 * send the message
	    	 */
	    	out.println(message.toString());
		}
	}

	/**
	 * receive message from server and take the appropriate action based on the message
	 * basically a huge case statement/demultiplexer at this time that could stand to be split up
	 */
	public void receiveMessage() {
		if (TheGame.GameState.SERVER_CONNECT_SCREEN != TheGame.gameState) {
			try {
				if (in.ready()) { //if there is a message for us
					String receivedStr = in.readLine();
					JSONObject received = (JSONObject) JSONValue.parse(receivedStr);
					//Gdx.app.log(getClass().getSimpleName(), "received: " + receivedStr);
					
					/*
					 * deal with chat messages received from server
					 * chat messages should be displayed in the chatbox - display is handled by the stage
					 */
					if (received.get("type").equals("chatMessage")) {
						String message = (String) received.get("message");
	
						stage.addMessageToChatbox(message);
					}
					
					//spin until receive message from server to start game (signaling that other client has connected, etc)
					if (GameState.IN_LOBBY == TheGame.gameState) {
	
						/*
						 * upon receiving a network message that the game has started, change the game state
						 */
						if (received.get("type").equals("gameStartSignal")) {
							theGame.transitionFromLobbyToInGame();
					
						/*
						 * upon receiving a network message that a remote player has joined the game
						 */
						} else if (received.get("type").equals("playerInfo")) {
							String remotePlayerName = (String) received.get("username");
							int remotePlayerUid = ((Number) received.get("uid")).intValue();
							//Player remotePlayer = theGame.addRemotePlayerToList(playerName, uid);
							LobbyPlayer remotePlayer = new LobbyPlayer(remotePlayerName, remotePlayerUid);

							stage.addPlayerToLobbyStage(remotePlayer);
							theGame.getLobbyManager().getLobbyPlayers().add(remotePlayer);
							System.out.println("added remotePlayer: " + remotePlayer);
							
						/*
						 * receive a network message that another player has changed their ready status, update the lobby UI
						 */
						} else if (received.get("type").equals("readyStatus")) {
							/*int uid = ((Number) received.get("uid")).intValue();
							boolean isReady = (Boolean) received.get("readyStatus");
							for (Player player: stage.playerToCheckBoxMap.keySet()) {
								if (player.uid == uid) {
									stage.playerToCheckBoxMap.get(player).setChecked(isReady);
								}
							}*/
							

						/*
						 * receive the class of a player (potentially the local player) from the server (mage, ranger, shield)
						 */
						} else if (received.get("type").equals("classAssignment")) {
							String newClassName = (String) received.get("class");

							
							int uid = ((Number) received.get("uid")).intValue();
							CurrentClass newClass = convertClassStringToEnum(newClassName);
							theGame.getLobbyManager().setClassByUid(uid, newClass);
							
							//stage.setClass(className);
							
						/*
						 * get your uid (the unique identifier that the server refers to thhis client by)
					     * you should receive your uid soon after connecting to the server
						 * this is where you instantiate the local lobbyplayer
						 */
						} else if (received.get("type").equals("uidUpdate")) {
							int uid = ((Number) received.get("uid")).intValue();
							LobbyPlayer localLobbyPlayer = new LobbyPlayer(theGame.getUsername(), uid);
							theGame.getLobbyManager().setLocalLobbyPlayer(localLobbyPlayer);
							stage.addPlayerToLobbyStage(localLobbyPlayer);
						/*
						 * receive information about what a connected player's username is
						 */
						} else if (received.get("type").equals("username")) {
							String username = (String) received.get("username");
							int uid = ((Number) received.get("uid")).intValue();
							theGame.getLobbyManager().setUsernameByUid(username, uid);
							stage.updateLobbyPlayerTable(uid);
						}
						
						
						
						
						
						
					} else if (GameState.GAME_STARTED == TheGame.gameState) { //handle messages that come during game play, after the game has started
		        		String messageType = (String) received.get("type");
		        		
		        		/*
		        		 * receive a position update for another player
		        		 */
		        		if (messageType.equals("position")) {
			        		double otherPlayerX = ((Number) received.get("charX")).floatValue();
			        		double otherPlayerY = ((Number) received.get("charY")).floatValue();
			        		int uid = ((Number) received.get("uid")).intValue();
			        		theGame.currentMap.getPlayerByUid(uid).setPos(new Point(otherPlayerX, otherPlayerY));
			        		
			        	/*
			        	 * receive information about what direction another player is facing
			        	 */
		        		} else if (messageType.equals("direction")) {
		        			int uid = ((Number) received.get("uid")).intValue();
		        			
		        			String directionStr = (String) received.get("direction");
		        			DirectionOfTravel direction = DirectionOfTravel.valueOf(directionStr);
		        			
		        			((Player) theGame.currentMap.getPlayerByUid(uid)).setDirection(direction);
		        			
		        		/*
		        		 * receive message that an item has been removed from the ground
		        		 */
		        		} else if (messageType.equals("removedItem")) {
		        			int uid = ((Number) received.get("uid")).intValue(); //unique identifier of item which was removed
		        			theGame.currentMap.itemsOnField.removeByUid(uid);
		        			stage.updateItemList(); //repopulate itemList to get rid of the listing for the removed item
		        			
		        		/*
		        		 * receive message that an item has been added to your inventory
		        		 */
		        		} else if (messageType.equals("inventoryAddition")) { //arrives before the removedItem message
		        			int uid = ((Number) received.get("uid")).intValue();
		        			//TODO AAAAAAAAA THE CHAINING
		        			theGame.currentMap.localPlayer.inv.addItem(theGame.currentMap.itemsOnField.getByUid(uid));
		        			
		        		/*
		        		 * receive message that an item has appeared on the ground
		        		 */
		        		} else if (messageType.equals("itemDrop")) {
		        			
		        			Item droppedItem = new Item(received); //create an item from the json
		        			Gdx.app.log(getClass().getSimpleName(), "dropped item: " + droppedItem);
		        			theGame.currentMap.getItemList().itemList.add(droppedItem);
		        		}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * take the string representing the new class of the player given to us in a network message and map it to a field
	 *  of the enum which represents the selected class of a lobby player
	 * @param newClassName the name of the class received in the network message
	 * @return the CurrentClass equivalent
	 */
	private CurrentClass convertClassStringToEnum(String newClassName) {
		if (newClassName.equals("ranger")) {
			return CurrentClass.RANGER;
		} else if (newClassName.equals("mage")) {
			return CurrentClass.MAGE;
		} else if (newClassName.equals("shield")) {
			return CurrentClass.SHIELD;
		}
		throw new IllegalArgumentException("ERROR! " + newClassName + " is not a supported class, please make sure the network message is correct");
		
	}

	public void setStage(ExtendedStage stage) {
		this.stage = stage;
	}
}
