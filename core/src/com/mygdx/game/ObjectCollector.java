////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     ObjectCollector.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;

import com.badlogic.gdx.utils.JsonValue;

/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
public class ObjectCollector
{
	protected List<GameObject> objectList;
	// 2d positional grid of objects
	// Currently, this will be one-to-one with the map grid aka
	//  for each tile in the map grid there is exactly one tile in the objectGrid
	// Each tile in the objectGrid will contain a List<GameObject> of the GameObjects which
	//  exist at least partially in that tile
	protected Object[][] objectGrid; //organize the objects by the what tile they're on. That way the game has a fast way of knowing what objects are on the floor that the player can reach
	private final int TILE_HEIGHT;
	private final int TILE_WIDTH;
	private int numMapCols;
	private int numMapRows;
	private Tile[][] tileMap;
	
	public ObjectCollector(int tileWidth, int tileHeight, int numCols, int numRows, Tile[][] tileMap)
	{
		this.numMapCols = numCols;
		this.numMapRows = numRows;
		this.tileMap = tileMap;
		objectGrid =  new Object[numRows][numCols];
		// prevent NPEs
		initializeObjectGridWithEmptyLists();
		objectList = new ArrayList<GameObject>();
		TILE_HEIGHT = tileHeight;
		TILE_WIDTH = tileWidth;
	}
	
	public ObjectCollector(List<GameObject> objects, int tileWidth, int tileHeight, int numCols, int numRows, Tile[][] tileMap)
	{
		objectList = objects;
		objectGrid = new Object[numRows][numCols];
		this.tileMap = tileMap;
		this.numMapCols = numCols;
		this.numMapRows = numRows;
		initializeObjectGridWithEmptyLists();
		for (GameObject object: objectList) {
			addObjectToGrid(object);
		}
		TILE_HEIGHT = tileHeight;
		TILE_WIDTH = tileWidth;
	}
	
	public void addObject(JsonValue objectMap)
	{
		/*
		 * get a json map of the object's basic properties
		 */
		JsonValue baseProperties = objectMap.get("baseProperties");
		boolean passable =  baseProperties.getBoolean("collision");
		int visLayer = baseProperties.getInt("visLayer");
		double xPos = baseProperties.getDouble("x");
		double yPos = baseProperties.getDouble("y");
		String fileName = baseProperties.getString("fileName");
		Point pos = new Point(xPos, yPos);
		
		/*
		 * TODO after the shape-maker is combined with the map tool, have object's shapes be part of their json
		 *  and make a  ShapeSerializer to read the shape
		 */
		List<LineSeg> shapeLineSegs = new ArrayList<LineSeg>();
		ObjectShape shape = new ObjectShape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 55)),
				new LineSeg(new Point(15, 55), new Point(50, 55)),
				new LineSeg(new Point(50, 55), new Point(50, 0)),
				new LineSeg(new Point(50, 0), new Point(15, 0))
				),
				new Point(0,0));
		
		GameObject newObject = new GameObject(passable, visLayer, pos, fileName, shape);
		objectList.add(newObject);
		addObjectToGrid(newObject);
		
	}
	public void addObjectToGrid(GameObject object) {
		// check every tile for intersection with object,
		//  and if there is intersection, add the object to the list
		//  of objects which intersect that tile for quick lookup later
		for (int r = 0; r < tileMap.length; r++) {
			for (int c = 0; c < tileMap[r].length; c++) {
				Tile tile = tileMap[r][c];
				if (object.intersects(tile)) {
					/*System.out.println(">>>> " + String.valueOf(r) + ", " + String.valueOf(c));
					System.out.println(tile.getShape());
					System.out.println("------------------------");
					System.out.println(object.getShape());*/
					((List<GameObject>) objectGrid[r][c]).add(object);
				}
			}
		}
	}
	
	private void initializeObjectGridWithEmptyLists() {
		for (int r = 0; r < tileMap.length; r++) {
			for (int c = 0; c < tileMap[r].length; c++) {
				objectGrid[r][c] = new ArrayList<GameObject>();
			}
		}
	}
	public void deleteItem(int index)
	{
		objectList.remove(index);
	}
	
	public void deleteItem(Item x)
	{
		objectList.remove(x);
	}
	
	public GameObject getObject(int index)
	{
		return objectList.get(index);
	}
	
	public String getObjectName(int index)
	{
		return objectList.get(index).getName();
	}
	
	public double getXPos(int index)
	{
		return objectList.get(index).getXPos();
	}
	
	public double getYPos(int index)
	{
		return objectList.get(index).getYPos();
	}
	public float getWidth(int index) {
		return getObject(index).getWidth();
	}
	public float getHeight(int index) {
		return getObject(index).getHeight();
	}
	
	public String getImage(int index)
	{
		return objectList.get(index).getImage();
	}
	public int getListSize()
	{
		return objectList.size();
	}
}
