package com.mygdx.game;

import java.io.File;
import java.io.FileNotFoundException;
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
    
    public Item(int id, int xPos, int yPos) throws FileNotFoundException
    {
    	this.id = id;
    	this.xPos = xPos;
    	this.yPos = yPos;
    	
    	Scanner itemFileScanner = new Scanner(new File("Item"));
    	
		//find corresponding line in item file
		String currentLine = null;
		String[] currentAttributes = null;
		boolean found = false;
		while (!found)
		{
			currentLine = itemFileScanner.nextLine();
			currentAttributes = currentLine.split(", ");
			int idFromItemFile = Integer.parseInt(currentAttributes[1]);
			if (idFromItemFile == this.id) { //id matches item we want
				found = true;
			}
		}
		itemFileScanner.close();
		//now currentAttributes should correspond to the attributes of the item we want from the item file
		setName(currentAttributes[0]);
		setId(Integer.parseInt(currentAttributes[1]));
		setInventoryImage(currentAttributes[2]);
		setFloorImageURI(currentAttributes[3]);
		setWidth(Integer.parseInt(currentAttributes[4]));
		setHeight(Integer.parseInt(currentAttributes[5]));
		
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
    public int getXPos()
    {
    	return xPos;
    }
    public int getYPos()
    {
    	return yPos;
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