package com.mygdx.server;

import java.net.*;
import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.LinkedList;

import org.json.simple.JSONObject;

import com.mygdx.game.player.Player;

public class PlayerClient {
	/**
	 * the username of the client
	 */
    String username;
    /**
     * the channel used to communicate with the server
     */
    SocketChannel socketChannel;
    /**
     * queue of messages from this client
     */
    Queue<JSONObject> messageInQueue;
    /**
     * if this client is ready to start the game
     */
    boolean ready;
    /**
     * the unique id of this client
     */
    int uid;
    /**
     * the player representing this client on the gamemap
     */
    Player player;

    public PlayerClient(SocketChannel socketChannel) {
    	this.socketChannel = socketChannel;
        messageInQueue =  new LinkedList<JSONObject>();
        ready = false;
    }
}