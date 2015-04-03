package com.mygdx.game;

public class Tile
{
    public String imageURI;
    private String name;
    private int id; 
    public String door;
    public int[] doorExitPos;
    private boolean passable;
    //public Item hazard;
    
    Tile(String imageURI, String name, boolean passable)
    {
        this.imageURI = imageURI;
        this.name = name;
        this.passable = passable;
    }
    
    public void setImageURI(String URI)
    {
        imageURI = URI;
    }
    
    public boolean isPassable() {
    	return passable;
    }
    public String getName() {
    	return name;
    }
}