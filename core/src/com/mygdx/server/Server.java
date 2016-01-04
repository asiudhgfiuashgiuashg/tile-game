package com.mygdx.server;

import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.ExtendedStage;
import com.mygdx.game.GameMap;
import com.mygdx.game.Item;
import com.mygdx.game.LocalPlayer;
import com.mygdx.game.Player;
import com.mygdx.game.RemotePlayer;

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

public class Server {
	private static final int MAX_CLIENTS = 999;
	private int port;
	List<PlayerClient> clients;
	private int numClientsConnected;
	private ServerSocketChannel serverSocketChannel;
	int currentUID; //used to give each client a  unique id (an integer which is incremented for every new connection)
	public GameMap gameMap;
	ExtendedStage stage;
	
	public Server(int port, ExtendedStage stage) {
		setupServer(port);
		currentUID = 0;
		this.stage = stage;
	}
	
	public void setupServer(int port) {
		this.port = port;
		clients = new ArrayList<PlayerClient>();
	    
	    int numClientsConnected = 0;
	    int currentUID = 0;
        
        try {
            serverSocketChannel =  ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port)); //give it da port
            serverSocketChannel.configureBlocking(false);
            
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
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
					}
				}

				//send newly connected player their UID;
				JSONObject uidInfo = new JSONObject();
				uidInfo.put("type", "uidUpdate");
				uidInfo.put("uid", playerClient.uid);
				sendJSONOnSocketChannel(uidInfo, playerClient.socketChannel);
				
        	}
    	}
	}

	public void dealWithMessages() {
		for (int i = 0; i < clients.size(); i++)  {
    		attemptReadFrom(clients.get(i));
    		handleMessageFrom(clients.get(i));
    	}
	}

    /**
    * remember there is one socketChannel per client
    */
    public void sendJSONOnSocketChannel(JSONObject jsonObj, SocketChannel socketChannel) {
    	Gdx.app.log(getClass().getSimpleName(), "sending " + jsonObj);
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

  


    public void attemptReadFrom(PlayerClient client) {
    	JSONObject receiveTo = new JSONObject();
    	try {
    		ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
			int numBytesRead = client.socketChannel.read(byteBuffer);
			if (numBytesRead > 0) {
				String receivedStr = new String(byteBuffer.array(), 0, numBytesRead, "ASCII");
				String lines[] = receivedStr.split("\\r?\\n");
				for (String line: lines) {                    
					//System.out.println("--------------------------------------");
					receiveTo = (JSONObject) JSONValue.parse(line);
					//System.out.println("received json value: " + receiveTo);
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

    public void handleMessageFrom(PlayerClient client) {
    	PlayerClient receiveFromClient = client;
        if (client.messageInQueue.peek() != null) { //something in queue
        	JSONObject received = client.messageInQueue.remove();
			//System.out.println("handling: " + received.toString());
			//position messages
			if (received.get("type").equals("position")) {
				//record position
				receiveFromClient.charX = ((Number) received.get("charX")).floatValue();
				receiveFromClient.charY = ((Number) received.get("charY")).floatValue();
				//System.out.println(received);
	    		// send coordinates to other clients
	    		sendToAllFrom(received, receiveFromClient);

			} else if (received.get("type").equals(("direction"))) { //direction updates, need to update animations accordingly
				DirectionOfTravel direction = DirectionOfTravel.valueOf(DirectionOfTravel.class, ((String) received.get("direction")));

				JSONObject animationObj = new JSONObject(); //represents a message signalling an animation change in the RemotePlayer
				animationObj.put("type", "animation");
				if (DirectionOfTravel.LEFT == direction) {
					animationObj.put("animationName", "walkLeft"); // these Animation names are recognized by the setAnimation method of RemotePlayer and signal it what animation to change the remotePlayer to
				} else if (DirectionOfTravel.RIGHT == direction) {
					animationObj.put("animationName", "walkRight");
				} else if (DirectionOfTravel.DOWN == direction) {
					animationObj.put("animationName", "walkDown");
				} else if (DirectionOfTravel.UP == direction) {
					animationObj.put("animationName", "walkUp");
				} else if (DirectionOfTravel.IDLE == direction) { //standing still
					animationObj.put("animationName", "idle");
				}
				animationObj.put("uid", receiveFromClient.uid);
				sendToAllFrom(animationObj, receiveFromClient);

			} else if (received.get("type").equals("playerInfo")) {
				receiveFromClient.username = (String) received.get("username");

				JSONObject playerInfo = new JSONObject();
				//send newly connected player's info to everyone else
				playerInfo.put("type", "playerInfo");
				playerInfo.put("username", receiveFromClient.username);
				playerInfo.put("uid", receiveFromClient.uid);
				sendToAllFrom(playerInfo, receiveFromClient);

			} else if (received.get("type").equals("readyStatus")) { //during lobby, wait for all players to be ready
				boolean ready = (Boolean) received.get("readyStatus");
				receiveFromClient.ready = ready;
				received.put("uid", receiveFromClient.uid);
				sendToAllFrom(received, receiveFromClient);
				if (allAreReady()) {
					endLobby();
				}
			} else if (received.get("type").equals("chatMessage")) {
				String message = (String) received.get("message");
				message = receiveFromClient.username + ": " + message;
				received.clear();
				received.put("type", "chatMessage");
				received.put("message", message);
				//System.out.println("sending chat message: " + received);
				sendToAllFrom(received, receiveFromClient);
				
			} else if (received.get("type").equals("sprite")) {
				JSONObject spriteInfo = new JSONObject();
				spriteInfo.put("type", "sprite");
				spriteInfo.put("uid", receiveFromClient.uid);
				spriteInfo.put("spriteID", (String) received.get("spriteID"));
				sendToAllFrom(spriteInfo, receiveFromClient);
				
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
					if (gameMap.player != playerToUpdate) {
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
				
			} else if (received.get("type").equals("itemDrop")) {
				//int itemUid = ((Number) received.get("uid")).intValue();
				//Player playerWhoDroppedItem = gameMap.getPlayerByUid(receiveFromClient.uid);
				//Item droppedItem = gameMap.getItemList().getByUid(itemUid);
				sendToAll(received);
			}
		}
    }


	public boolean allAreReady() {
    	for (PlayerClient client: clients) {
    		if (!client.ready) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * let everyone know the game is starting
     */
    public void endLobby() {
		JSONObject readyObject = new JSONObject();
		readyObject.put("type", "gameStartSignal");
		for (PlayerClient client: clients) {
			System.out.println("sending");
        	sendJSONOnSocketChannel(readyObject, client.socketChannel);
    	}
    }

    /**
     * send a message to everyone except the from client
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
    
    public void sendToAll(JSONObject toSend) {
    	for (PlayerClient client: clients) {
    		sendJSONOnSocketChannel(toSend, client.socketChannel);
    	}
    }
    
    public void sendToAllExceptHost(JSONObject toSend) {
    	for (PlayerClient client: clients) {
    		if (gameMap.getPlayerByUid(client.uid) != gameMap.player){
    			sendJSONOnSocketChannel(toSend, client.socketChannel);
    		}
    	}
    }
}
