package com.mygdx.game;

import java.io.File;
import java.util.Scanner;

public class Item {
    private String name;
    private int id;
    private int xPos;
    private int yPos;

    public static final String classFileLocation = "../core/assets/Item.txt";

    private String inventoryImageURI;
    private String floorImageURI;
    
    private int width, height;
    
    Item(int id, int xPos, int yPos)
    {
    	this.id = id;
    	this.xPos = xPos;
    	this.yPos = yPos;
    	
    	Scanner itemFileScanner = new Scanner(new File("Item"));
    	
		//find corresponding line in tileFile
		String currentLine = null;
		String[] currentAttributes = new String[0];
		boolean found = false;
		while (!found)
		{
			currentLine = itemFileScanner.nextLine();
			currentAttributes = currentTilesLine.split(", ");
			if (currentAttributes[1].equals(individualTileId)) { //id matches tile we want
				found = true;
			}
		}
    }
    
    public int getId() {
        return id;    
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public String getName() {
        return name;
    }
    public String getInventoryImage()
    {
        return inventoryImageURI;
    }
 
    public String getFloorImage()
    {
        return floorImageURI;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    
    public void setInventoryImage(String URI)
    {
        this.inventoryImageURI = URI;
    }
    
    public void setFloorImageURI(String URI)
    {
        this.floorImageURI = URI;   
    }
    
}