package com.mygdx.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameObject;
import com.mygdx.game.LineSeg;
import com.mygdx.game.Point;
import com.mygdx.game.Shape;
import com.mygdx.game.GameMap;
import com.badlogic.gdx.ai.pfa.Connection;

public class GraphCreator {
    private static final int NODE_SPACING = 20;
    
    public static IndexedGraph<PositionIndexedNode> graphFromMap(GameMap map) {
        int mapWidth = GameMap.TILE_WIDTH * map.numCols;
        int mapHeight = GameMap.TILE_HEIGHT * map.numRows;
        
        
      	int index = 0; // in an IndexedGraph, each node needs to have an index (sequential, starting at 0)
        Array<PositionIndexedNode> nodes = new Array<PositionIndexedNode>();
        for (int x = 0; x < mapWidth; x += NODE_SPACING) {
            for (int y = 0; y < mapHeight; y+= NODE_SPACING) {
                PositionIndexedNode node = new PositionIndexedNode(x, y, index);
                nodes.add(node);
                index++;
            }
        }
        for (int i = 0; i < nodes.size; i++) {
        	PositionIndexedNode node = nodes.get(i);
            for (int j = 0; j < nodes.size; j++) {
            	PositionIndexedNode otherNode = nodes.get(j);
                if (node != otherNode) { //don't connect a node to itself
                    if (canDrawLineBetween(node, otherNode, map)) {
                       node.connectToBidirectionally(otherNode);
                    }
                }
            }
        }
        
        //checks for nodes with no connections, and then removes them from the array
        Array<Integer> indexValues = new Array<Integer>();
        for(PositionIndexedNode node: nodes) {
            if(node.getConnections().size == 0) {
                indexValues.add(nodes.indexOf(node, true)); //the boolean has to do with using .equals or == to do the search.
                											//true is .equals, this might be the incorrect search to use.
            }
        }
        for(int indexVal: indexValues) {
            nodes.removeIndex(indexVal);
        }
        
        IndexedGraph<PositionIndexedNode> graph = new DefaultIndexedGraph<PositionIndexedNode>(nodes);
        return graph;
    }
    
    //creates a GameObject using the two node points given, and then sees if it intersects with any of the objects on the map
    public static boolean canDrawLineBetween(PositionIndexedNode firstNode, PositionIndexedNode secondNode, GameMap map) {
        List<LineSeg> list = new ArrayList<LineSeg>();
        list.add(new LineSeg(new Point(firstNode.x, firstNode.y), new Point(secondNode.x, secondNode.y)));
        
        GameObject line = new GameObject(new Shape(list, new Point(0, 0)), false); //Not sure how to get the node position
        boolean valid = true;
        
        for (int x = 0; x < map.objectList.getListSize(); x++) {
            if(line.intersects(map.objectList.getObject(x))) {
                valid = false;
            }
        }
        
        return valid;
    }
}

