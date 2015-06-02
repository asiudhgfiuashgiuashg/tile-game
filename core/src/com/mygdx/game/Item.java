package com.mygdx.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.badlogic.gdx.Gdx;

public class Item {
    private String name;
    private int id;
    private Point pos;

    public static final String classFileLocation = "../core/assets/Item.txt";

    private String inventoryImageURI;
    private String floorImageURI;
    
    private int width, height;
    
    public Item(int id, Point pos) throws FileNotFoundException
    {
    	this.id = id;
    	this.pos = pos;
    	
    	Scanner itemFileScanner = new Scanner(new File(classFileLocation));

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
        return ("../core/assets/" + floorImageURI);
    }
    public float getXPos()
    {
    	return pos.getX();
    }
    public float getYPos()
    {
    	return pos.getY();
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
    
    @Override
    public String toString() {
    	return name.replace("_", " ");
    }
    
}