package com.mygdx.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * represents a 2d coordinate
 * @author elimonent
 *
 */
public class PositionIndexedNode implements IndexedNode<PositionIndexedNode> {

	public float x;
	public float y;
	public int index;
	public Array<Connection<PositionIndexedNode>> connections; //a resizeable array provided by libgdx
																//array of connections outgoing from this IndexedNode.
	
	public PositionIndexedNode(float x, float y, int index) {
		this.x = x;
		this.y = y;
		this.index = index;
		connections = new Array<Connection<PositionIndexedNode>>();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Array<Connection<PositionIndexedNode>> getConnections() {
		return connections;
	}
	
	public void connectToBidirectionally(PositionIndexedNode other) {
		Connection<PositionIndexedNode> outgoingConnection = new DefaultConnection<PositionIndexedNode>(this, other); //default cost of 1
		this.connections.add(outgoingConnection);
		Connection<PositionIndexedNode> incomingConnection = new DefaultConnection<PositionIndexedNode>(other, this);
		other.connections.add(incomingConnection);
	}
	
	public void connectTo(PositionIndexedNode other) {
		Connection<PositionIndexedNode> outgoingConnection = new DefaultConnection<PositionIndexedNode>(this, other);
		this.connections.add(outgoingConnection);
	}
	
	public void draw(ShapeRenderer renderer, int xOffset, int yOffset) {
		renderer.circle(x + xOffset, y + yOffset, 12);
	}
	
	@Override
	public String toString() {
		return "[node] x: " + x + " y: " + y;
	}
}
