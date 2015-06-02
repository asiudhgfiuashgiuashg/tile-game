////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     Object.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
public class Object
{
	private String name;
    private int id;
    private Point pos;
    
    public static final String classFileLocation = "../core/assets/Object.txt";
    
	Shape hitbox;
	String imageURI;
	
	private int width, height;
	
	public Object(int id, Point pos) throws FileNotFoundException
    {
    	this.id = id;
    	this.pos = pos;
    	
    	Scanner objectFileScanner = new Scanner(new File(classFileLocation));

		//find corresponding line in object file
		String currentLine = null;
		String[] currentAttributes = null;
		boolean found = false;
		while (!found)
		{
			currentLine = objectFileScanner.nextLine();
			currentAttributes = currentLine.split(", ");
			int idFromObjectFile = Integer.parseInt(currentAttributes[1]);
			if (idFromObjectFile == this.id) { //id matches object we want
				found = true;
			}
		}
		objectFileScanner.close();
		//now currentAttributes should correspond to the attributes of the object we want from the object file
		setName(currentAttributes[0]);
		setId(Integer.parseInt(currentAttributes[1]));
		setImage(currentAttributes[2]);
		List<LineSeg> lines = new ArrayList<LineSeg>();
		List<Point> points = new ArrayList<Point>();
		for (int x = 3; x < currentAttributes.length; x += 2)
		{
			points.add(new Point(Float.parseFloat(currentAttributes[x]), Float.parseFloat(currentAttributes[x+1])));
		}
		for (int i = 0; i < points.size(); i++)
		{
			lines.add( new LineSeg(points.get(i), points.get(i+1)) );
		}
		lines.add( new LineSeg(points.get(points.size() - 1), points.get(0)) );
		
		hitbox = new Shape(lines, pos);
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
    public String getImage()
    {
        return imageURI;
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
    
    public void setImage(String URI)
    {
        this.imageURI = URI;
    }
    
    
    @Override
    public String toString() {
    	return name.replace("_", " ");
    }
	
}
