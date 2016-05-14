package com.mygdx.game;

import java.util.Arrays;

public class Tile extends GameObject
{
    private static final int TILE_WIDTH = 80;
    private static final int TILE_HEIGHT = 80;

    public String imageURI;
    private String name;
    private int id; 
    public String door;
    public int[] doorExitPos;
    private boolean passable;
    //public Item hazard;
    
    public Tile(int xPos, int yPos, String imageURI, String name, boolean passable)
    {
        this(makeShape(xPos, yPos), imageURI, name, passable);
    }
    
    public Tile(Shape tileShape, String imageURI, String name, boolean passable)
    {
        super(tileShape, passable);
        this.imageURI = imageURI;
        this.name = name;
        this.passable = passable;
    }
    private static Shape makeShape(int xPos, int yPos) {
    	Shape tileShape = new Shape(Arrays.asList(
                new LineSeg(new Point(0,0), new Point(0, TILE_HEIGHT)),
                new LineSeg(new Point(0, TILE_HEIGHT), new Point(TILE_WIDTH, TILE_HEIGHT)),
                new LineSeg(new Point(TILE_WIDTH, TILE_HEIGHT), new Point(TILE_WIDTH, 0)),
                new LineSeg(new Point(TILE_WIDTH, 0), new Point(0, 0))),

                new Point(xPos, yPos)); //a square (assuming TILE_WIDTH == TILE_HEIGHT
    	return tileShape;
    }
    
    
    public void setImageURI(String URI)
    {
        imageURI = URI;
    }
    
    public String getName() {
    	return name;
    }
    public static int getTileWidth() {
        return TILE_WIDTH;
    }
    public static int getTileHeight() {
        return TILE_HEIGHT;
    }
}
