package com.mygdx.game;

public class Tile
{
    public String imageURI;
    private String name;
    private int id; 
    public String door;
    public int[] doorExitPos;
    public boolean leftWall;
    public boolean rightWall;
    public boolean topWall;
    public boolean bottomWall;
    //public Item hazard;
    
    Tile(String imageURI, String name, boolean leftWall, boolean rightWall, boolean topWall, boolean bottomWall)
    {
        this.imageURI = imageURI;
        this.name = name;
        this.leftWall = leftWall;
        this.rightWall = rightWall;
        this.topWall = topWall;
        this.bottomWall = bottomWall;
    }
    
    public void setImageURI(String URI)
    {
        imageURI = URI;
    }
    
    public boolean hasLeftWall() {
    	return leftWall;
    }
    public boolean hasRightWall() {
    	return rightWall;
    }
    public boolean hasTopWall() {
    	return topWall;
    }
    public boolean hasBottomWall() {
    	return bottomWall;
    }
    public String getName() {
    	return name;
    }
}