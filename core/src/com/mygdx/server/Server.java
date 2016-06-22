package com.mygdx.server;

import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.ExtendedStage;
import com.mygdx.game.GameMap;
import com.mygdx.game.Item;
import com.mygdx.game.player.*;
import com.mygdx.game.serializers.GameMapSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Queue;
import java.util.LinkedList;
import java.net.StandardSocketOptions;
import java.net.SocketOption;

/**
 * maintains the game state.
 * Updates the game state by simulating the world and receiving player actions over the network
 * Sends info about changes in game state to connected players;
 * @author elimonent
 *
 */
public class Server {
	/**
	 * the most players that can connect to the server
	 */
	private static final int MAX_CLIENTS = 3;
	/**
	 * the port that the server listens on
	 */
	private int port;
	/**
	 * the remote clients who are connected to the server
	 */
	List<PlayerClient> clients;
	private int numClientsConnected;
	/**
	 * the server listens for connections on this channel
	 */
	private ServerSocketChannel serverSocketChannel;
	int currentUID; //used to give each client a  unique id (an integer which is incremented for every new connection)
	/**
	 * the server's representation of the game map. used to keep track of game state
	 */
	private GameMap gameMap;
	ExtendedStage stage;
	/**
	 * the name of the map.
	 * change this to change what map is loaded.
	 */
	private final String mapName = "test";
	
	/**
	 * if a position message is from > this long ago, throw it away and get another message
	 */
	private static final int MAX_POS_UPDATE_AGE = 200;
	
	/**
	 * if a direction message is from > this long ago, throw it away and get another message
	 */
	private static final int MAX_DIR_UPDATE_AGE = 200;

	
	public Server(int port, ExtendedStage stage) {
		setupServer(port);
		currentUID = 0;
		this.stage = stage;
		Json json = new Json();
		json.setSerializer(GameMap.class, new GameMapSerializer());
        this.gameMap = json.fromJson(GameMap.class, Gdx.files.internal(mapName + ".json"));
	}
	
	/**
	 * start listening for connections
	 * @param port teh port to listen on
	 */
	public void setupServer(int port) {
		this.port = port;
		clients = new ArrayList<PlayerClient>();
	    
	    numClientsConnected = 0;
	    currentUID = 0;
        
        try {
            serverSocketChannel =  ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port)); //give it da port
            serverSocketChannel.configureBlocking(false);
            
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * check for connection requests from potential clients (players who want to join the game and are trying to connect)
	 */
	public void checkForConnections() {
    	if (numClientsConnected < MAX_CLIENTS) {
    		SocketChannel socketChannel = null;
    		try {
				socketChannel =  serverSocketChannel.accept();
			} catch (IOException e) {
				e.printStackTrace();
			} 
    		
    		
    		//see if a connection is immediately available, will return null if one isnt (non-blocking I/O)
    		PlayerClient playerClient = null;
    		if (socketChannel != null) { //got a connection
        		//Socket clientSocket = socketChannel.socket();
        		try {
					socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
         
	        		playerClient = new PlayerClient(socketChannel);
	        		socketChannel.configureBlocking(false);
        		} catch (IOException e) {
	        		e.printStackTrace();
	        	}
        		
        		playerClient.socketChannel = socketChannel;
        		playerClient.uid = currentUID;
        		currentUID++;
        		
        		/*
        		 * temporary non negotiable class assignmetns for now
        		 *  later implement players able to choose classes
        		 */
        		if (0 == numClientsConnected) {
        			playerClient.player = new RangerClass(playerClient.uid);
        		} else if (1 == numClientsConnected) {
        			playerClient.player = new MageClass(playerClient.uid);
        		} else if (2 == numClientsConnected) {
        			playerClient.player = new ShieldClass(playerClient.uid);
        		}
        		
        		clients.add(playerClient);

        		Gdx.app.log(getClass().getSimpleName(), "client number " + String.valueOf(numClientsConnected) + " connected");
        		numClientsConnected++;
        		
        		//give newly connected player the player info of everyone else
				JSONObject playerInfo = new JSONObject();
				for (PlayerClient client: clients) {
					if (!client.equals(playerClient)) {
						playerInfo.put("type", "playerInfo");
						playerInfo.put("username", client.username);
						playerInfo.put("uid", client.uid);
						sendJSONOnSocketChannel(playerInfo, playerClient.socketChannel);
						playerInfo.clear();
						
						/*
						 * let everyone (especially the newly-connected client) know what the class assignment of this player is
						 * in the future, this will be called as players change their selected classes
						 */
						sendClassAssignment(client);
					}
				}
				
				/*
				 * let everyone know that a new player with an unknown username has connected
				 */
				JSONObject newPlayerMsg = new JSONObject();
				newPlayerMsg.put("type", "playerInfo");
				newPlayerMsg.put("uid", playerClient.uid);
				sendToAllFrom(newPlayerMsg, playerClient); //send the uid of thhe new playeyr to everyone but the player himself
				
				//send newly connected player their UID;
				JSONObject uidInfo = new JSONObject();
				uidInfo.put("type", "uidUpdate");
				uidInfo.put("uid", playerClient.uid);
				sendJSONOnSocketChannel(uidInfo, playerClient.socketChannel);
				
				/*
				 * let everyone know what the class assignment of this player is
				 * in the future, this will be called as players change their selected classes
				 */
				sendClassAssignment(playerClient);
				
				
				/*
				 * add the new player to the map
				 */
				gameMap.addPlayer(playerClient.player);
				
        	}
    	}
	}
	
	/**
	 * call this function when transitioning from the lobby to the game in order to let
	 *   everyone know whwat class(mage ^ ranger ^ shield) they and their fellow lobbymates have been assigned 
	 * @param client the client whose calss assignment wiwll be broadcast
	 */
	protected void sendClassAssignment(PlayerClient client) {
		JSONObject classMessage = new JSONObject();
		classMessage.put("type", "classAssignment"); //let the client know what the purpose of this network message is
		
		if (client.player instanceof MageClass) {
			classMessage.put("class", "mage");
		} else if (client.player instanceof RangerClass) {
			classMessage.put("class", "ranger");
		} else if (client.player instanceof ShieldClass) {
			classMessage.put("class", "shield");
		}
		classMessage.put("uid", client.uid);
		sendToAll(classMessage);
	}

	public void dealWithMessages() {
		for (int i = 0; i < clients.size(); i++)  {
    		attemptReadFrom(clients.get(i));
    		handleMessageFrom(clients.get(i));
    	}
	}

    /**
     * sends a message to a single client using their associated socketChannel (there is one socketChannel per client)
     */
    public void sendJSONOnSocketChannel(JSONObject jsonObj, SocketChannel socketChannel) {
    	//Gdx.app.log(getClass().getSimpleName(), "sending " + jsonObj);
    	String stringToSend = jsonObj.toString() + '\n';
    	ByteBuffer toSend = ByteBuffer.allocate(stringToSend.length());	
		toSend.clear();
		try {
			toSend.put(stringToSend.getBytes("ASCII"));
			toSend.flip();
			while (toSend.hasRemaining()) {
				socketChannel.write(toSend);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
    }

  


    /**
     * see if there are any messages sent to us by this client
     * if there are messages, put them in a queue for this client to be handled later
     * @param client
     */
    public void attemptReadFrom(PlayerClient client) {
    	JSONObject receiveTo = new JSONObject();
    	try {
    		ByteBuffer byteBuffer = ByteBuffer.allocate(10000); //subbject to overflow
			int numBytesRead = client.socketChannel.read(byteBuffer);
			if (numBytesRead > 0) {
				String receivedStr = new String(byteBuffer.array(), 0, numBytesRead, "ASCII");
				String lines[] = receivedStr.split("\\r?\\n");
				for (String line: lines) {                    
					//System.out.println("--------------------------------------");
					receiveTo = (JSONObject) JSONValue.parse(line);
					//System.out.println("received json value: " + receiveTo);
					/*
					 * this value may be used to decide if a position or direction message is too old to be useful
					 */
					receiveTo.put("receivedTimeMillis", System.currentTimeMillis());
					client.messageInQueue.add(receiveTo);

                    if(receiveTo == null)
                    {
                        System.out.println("\nReceived a null error. Below is receivedStr, printed out");
                        System.out.println(receivedStr);
                        for(int x = 0; x < lines.length; x++){
                        System.out.printf("Line %d: %s\n", x, lines[x]);
                        }

                    }
                    
				}
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
    }

    /**
     * look at this client's message queue and handle the first message
     * aka take the appropriate action based on the message
     * For example, if the message is an update on the client's position, send the new position to other players
     *  and update the Server's model of the map
     * Currently a big case statement. Could be split up.
     * @param client
     */
    public void handleMessageFrom(PlayerClient client) {
    	PlayerClient receiveFromClient = client;
    	/*
    	 * mark this as true when a message is handled
    	 * some messages will not be handled, such as position updates that are too old. They will be thrown away. 
    	 * This way the
    	 *  message queue won't get backed up as easily
    	 */
    	boolean messageHandled = false;
        while (client.messageInQueue.peek() != null && false == messageHandled) { //something in queue
        	JSONObject received = client.messageInQueue.remove();
			//System.out.println("handling: " + received.toString());
			
        	long timeMillis = System.currentTimeMillis();
        	long receivedTime = ((Number) received.get("receivedTimeMillis")).longValue();
        	//position messages
			if (received.get("type").equals("position")) {
				/*
				 * check how long ago this update was received and potentially ignore it
				 */
				if (timeMillis - receivedTime <= MAX_POS_UPDATE_AGE) {
					//record position
					receiveFromClient.player.setX(((Number) received.get("charX")).floatValue());
					receiveFromClient.player.setY(((Number) received.get("charY")).floatValue());
					//System.out.println(received);
		    		// send coordinates to other clients
		    		sendToAllFrom(received, receiveFromClient);
		    		messageHandled = true;
				} else {
					messageHandled = false;
				}

			} else if (received.get("type").equals(("direction"))) { //direction updates
				/*
				 * check how long ago this update was received and potentially ignore it
				 */
				if (timeMillis - receivedTime <= MAX_DIR_UPDATE_AGE) {
					received.put("uid", receiveFromClient.uid); //so thhe clients know what client this direction update is from
					sendToAllFrom(received, receiveFromClient);
					messageHandled = true;
				} else {
					messageHandled = false;
				}

			} else if (received.get("type").equals("playerInfo")) {
				receiveFromClient.username = (String) received.get("username");

				JSONObject playerInfo = new JSONObject();
				//send newly connected username to everyone else once we learn what they want their username to be
				playerInfo.put("type", "username");
				playerInfo.put("username", receiveFromClient.username);
				playerInfo.put("uid", receiveFromClient.uid);
				sendToAllFrom(playerInfo, receiveFromClient);
				messageHandled = true;

			} else if (received.get("type").equals("readyStatus")) { //during lobby, wait for all players to be ready
				boolean ready = (Boolean) received.get("readyStatus");
				receiveFromClient.ready = ready;
				received.put("uid", receiveFromClient.uid);
				sendToAllFrom(received, receiveFromClient);
				if (allAreReady()) {
					endLobby();
				}
				messageHandled = true;
			} else if (received.get("type").equals("chatMessage")) {
				/*
				 * chat messages are broadcast to everyone including the sender
				 *  the sender wont display the message they send until it is received from the server (just simpler that way)
				 */
				String message = (String) received.get("message");
				message = receiveFromClient.username + ": " + message;
				received.clear();
				received.put("type", "chatMessage");
				received.put("message", message);
				sendToAll(received);
				messageHandled = true;
			} else if (received.get("type").equals("sprite")) {
				JSONObject spriteInfo = new JSONObject();
				spriteInfo.put("type", "sprite");
				spriteInfo.put("uid", receiveFromClient.uid);
				spriteInfo.put("spriteID", (String) received.get("spriteID"));
				sendToAllFrom(spriteInfo, receiveFromClient);
				messageHandled = true;
			} else if (received.get("type").equals("itemPickupRequest")) { //client asks to pick up an item on the ground
				int uid = ((Number) received.get("uid")).intValue();
				
				//remove item from game map
				Item removed = gameMap.getItemList().removeByUid(uid);
				stage.updateItemList(); //update host's item list
				if (null != removed) { //remove item from server's/hosting client's version of map
					
					
					//make appropriate updates to Player's inventory in the host's version of the game
					Player playerToUpdate = gameMap.getPlayerByUid(receiveFromClient.uid);
					playerToUpdate.inv.addItem(removed);
					
					//send message telling player who picked up item that it is in their inventory
					if (gameMap.localPlayer != playerToUpdate) {
						JSONObject inventoryAdditionMessage = new JSONObject();
						inventoryAdditionMessage.put("type", "inventoryAddition");
						inventoryAdditionMessage.put("uid", uid);
						sendJSONOnSocketChannel(inventoryAdditionMessage, receiveFromClient.socketChannel);
					}
					
					//send message to everyone except host that the item is no longer on the ground
					JSONObject removedItemMessage = new JSONObject();
					removedItemMessage.put("type", "removedItem");
					removedItemMessage.put("uid", uid);
					sendToAllExceptHost(removedItemMessage);
				}
				messageHandled = true;
			} else if (received.get("type").equals("itemDrop")) {
				//int itemUid = ((Number) received.get("uid")).intValue();
				//Player playerWhoDroppedItem = gameMap.getPlayerByUid(receiveFromClient.uid);
				//Item droppedItem = gameMap.getItemList().getByUid(itemUid);
				sendToAll(received);
				messageHandled = true;
			}
		}
    }

    /**
     * check if all of the clients in the lobby are ready for the game to start
     * @return true if everybody is ready to start the game
     */
	public boolean allAreReady() {
    	for (PlayerClient client: clients) {
    		if (!client.ready) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * let everyone know the game is starting then let them know for sure what class everyone is playing (every client will learn what class every client is playing)
     */
    public void endLobby() {
    	/*
    	 * next send everyone the signal to start the game
    	 */
		JSONObject readyObject = new JSONObject();
		readyObject.put("type", "gameStartSignal");
		for (PlayerClient client: clients) {
			System.out.println("sending");
        	sendJSONOnSocketChannel(readyObject, client.socketChannel);
    	}
		
    }

    /**
     * send a message to everyone except the from a client
     * will send this message to everyone except the sender
     * @param toSend
     * @param from
     */
    public void sendToAllFrom(JSONObject toSend, PlayerClient from) {
    	for (PlayerClient client: clients) {
			if (client != from) {
        		sendJSONOnSocketChannel(toSend, client.socketChannel);
        	}
    	}
    }
    
    /**
     * send a message to every connected client
     * @param toSend
     */
    public void sendToAll(JSONObject toSend) {
    	for (PlayerClient client: clients) {
    		sendJSONOnSocketChannel(toSend, client.socketChannel);
    	}
    }
    
    /**
     * needs to be removed. The client whho also happens to be running a server (hosting) shouldn't be treated specially
     * @param toSend
     */
    public void sendToAllExceptHost(JSONObject toSend) {
    	for (PlayerClient client: clients) {
    		if (gameMap.getPlayerByUid(client.uid) != gameMap.localPlayer){
    			sendJSONOnSocketChannel(toSend, client.socketChannel);
    		}
    	}
    }
}
