////////////////////////////////////////////////////////////////////////////////
//  Project:  theGame-core
//  File:     Map.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
public class Map
{
    private String title;
    private int numRows;
    private int numCols;
    private static final int TILE_WIDTH = 80;
    private static final int TILE_HEIGHT = 80;
    private Tile[][] mapTiles;
    private int mapWidth;
    private int mapHeight;
    private boolean mapMoveLeft;
    private boolean mapMoveRight;
    private boolean mapMoveUp;
    private boolean mapMoveDown;
    private boolean smallWidth;
	private boolean smallHeight;
	float stateTime;
    
    private int mapPosX;
    private int mapPosY;

    private int winX = 400;
    private int winY = 240;
    Player player;
    
    Texture mapImage;
    TextureRegion fov;
    ItemCollector itemsOnField;
    
    
    public Map(String mapFile, String tileFile, Player player) throws IOException
    {	
    	this.player = player;
    	player.setCurrentMap(this);
    	
        ///////////////////////////////////
        // convert mapFile into Tile[][] //
        ///////////////////////////////////
        Scanner mapFileScanner = new Scanner(new File(mapFile));
        title = mapFileScanner.nextLine();
        numRows = Integer.parseInt(mapFileScanner.nextLine());
        numCols = Integer.parseInt(mapFileScanner.nextLine());
        mapTiles = new Tile[numRows][numCols];
        stateTime = 0f;
        
        Scanner tileFileScanner = new Scanner(new File(tileFile));
        for (int r = 0; r < numRows; r++) {
            String currentRow = mapFileScanner.nextLine();
            String[] individualIds = currentRow.split("\\s+");
        	for (int c = 0; c < numCols; c++/*ha*/) {
        		String individualTileId = individualIds[c];
        		//find corresponding line in tileFile
        		String currentTilesLine = null;
        		String[] currentAttributes = null;
        		boolean found = false;
        		while (!found)
        		{
        			currentTilesLine = tileFileScanner.nextLine();
        			currentAttributes = currentTilesLine.split(", ");
        			if (currentAttributes[1].equals(individualTileId)) { //id matches tile we want
        				found = true;
        			}
        		}
        		//now currentAttributes should have all the attributes needed to create our tile object
        		//name, id, imageURI, left, right, top, bottom <--- order in Tiles.txt
        		//String imageURI, String name, boolean leftWall, boolean rightWall, boolean topWall, boolean bottomWall <----- order in Tile()
        		String imageURI = currentAttributes[2];
        		String name = currentAttributes[0];
        		boolean passable = Boolean.parseBoolean(currentAttributes[3]);
        		//need to also get hazard once class Item is implemented
        		Tile newTile = new Tile(imageURI, name, passable);
        		mapTiles[r][c] = newTile;
        		tileFileScanner = new Scanner(new File(tileFile));
        	}
        }
        
        tileFileScanner.close();
        
        //Initializes the ItemCollector object before giving it the information to create an array list of items.
        itemsOnField = new ItemCollector();
        int itemIndex = Integer.parseInt(mapFileScanner.nextLine());
        String[] currentAttributes = null;
        String className;
        int id;
        int xPos;
        int yPos;
        if (itemIndex > 0)
        {
        	for(int x = 0; x < itemIndex; x++)
        	{
        	String currentLine = mapFileScanner.nextLine();
			currentAttributes = currentLine.split(", ");
			className = currentAttributes[0];
			id = Integer.parseInt(currentAttributes[1]);
			xPos = Integer.parseInt(currentAttributes[2]);
			yPos = Integer.parseInt(currentAttributes[3]);
			itemsOnField.addItem(className, id, xPos, yPos);
        	}
			
        }
        
        mapFileScanner.close();
        ///////////////////////////////////////////////////////////
        // create and save png which is composite of tile images //
        ///////////////////////////////////////////////////////////
        System.out.println("CREATING COMPOSITE MAP IMAGE");
        BufferedImage bigImage = new BufferedImage(TILE_WIDTH * numCols, TILE_HEIGHT * numRows, BufferedImage.TYPE_INT_ARGB); //made up of combined tiles
        Graphics g = bigImage.getGraphics();
        for (int r = 0; r < numRows; r++)
        {
            for (int c = 0; c < numCols; c++)
            {
                BufferedImage currTileImg;
                currTileImg = ImageIO.read(new File("../core/assets/" + mapTiles[r][c].imageURI));
                g.drawImage(currTileImg, c * TILE_WIDTH, r * TILE_HEIGHT, null);
            }
        }
        
        try
        {
	        ImageIO.write(bigImage, "PNG", new File(title + ".png"));
	        mapImage = new Texture(Gdx.files.internal(title + ".png"));
        }
        catch(IOException e)
        {
        	System.out.println("Fucking sucks");
        }
        mapWidth = TILE_WIDTH * numCols;
        mapHeight = TILE_HEIGHT * numRows;

        initialCharPos();
        updatePosY(0);
        updatePosX(0);
        
            
        fov = new TextureRegion(mapImage, mapPosX, mapPosY, 2 * winX, 2 * winY);
    }
    
    public ItemCollector getItemList()
    {
    	return itemsOnField;
    }
    
    //updating and drawing the visible part of the map
    
    
    public void update(SpriteBatch batch)
    {
    	//All numbers in the y direction go from bottom to top in all other functions, but the final value is inverted within the the below function for proper usage.
        fov.setRegion(mapPosX, mapHeight - (mapPosY + 2*winY), 2*winX, 2*winY);
        stateTime += Gdx.graphics.getDeltaTime();
        player.update(stateTime);

        for (int x = 0; x < itemsOnField.getItemListSize(); x++)
        {
        	//System.out.println("My shoulder's shot!");
    		
        	if (itemsOnField.getXPos(x) + itemsOnField.getWidth(x) > mapPosX && itemsOnField.getXPos(x) < mapPosX + 2*winX)
        	{
        		
        		//System.out.println("X value is Key");
        		if(itemsOnField.getYPos(x) + itemsOnField.getHeight(x) > mapPosY && itemsOnField.getYPos(x) < mapPosY + 2*winX)
        		{
        			batch.draw(new Texture(itemsOnField.getFloorImage(x)), itemsOnField.getXPos(x) - mapPosX, itemsOnField.getYPos(x) - mapPosY);
        			//System.out.println("No! Y Value is Best!");
        		}
        	
        		
        	}
        }
    }
    
    public void draw(SpriteBatch batch)
    {
    	
        batch.draw(fov, 0, 0);

		player.draw(batch);
		
        
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    // set's the map and character draw positions based on char position //
    ///////////////////////////////////////////////////////////////////////
    
    public void initialCharPos()
    {
    	
    	if (mapWidth < 2*winX)
    	{
    		smallWidth = true;
    	}
    	else 
    	{
    		smallWidth = false;
    	}
    	if (mapHeight < 2*winY)
    	{
    		smallHeight = true;
    	}
    	else
    	{
    		smallHeight = false;
    	}
    	
    	adjustCharPlacement();			//Omit this line if movement testing with exact coordinates is being done,
    									//as this method just provides a fail safe in case the char is placed off the map

    	
    	if (player.posX < winX && smallWidth == false)
    	{
    		player.drawPosX = player.posX;
    	}
    	else if (player.posX > mapWidth - winX && smallWidth == false)
    	{
    		player.drawPosX = (player.posX - mapWidth + 2*winX);
    	}
    	
    	
    	if (player.posY < winY && smallHeight == false)
    	{
    		player.drawPosY = player.posY;
    	}
    	else if (player.posY > mapHeight - winY && smallHeight == false)
    	{
    		player.drawPosY = (player.posY - (mapHeight - winY) + winY);
    	}
    	if (smallWidth == true)
    	{
    		mapPosX = mapWidth/2 - winX;
    	}
    	if (smallHeight == true)
    	{
    		mapPosY = mapHeight/2 - winY;
    	}
    }
    
    
    public void adjustCharPlacement()
    {
    	if (player.posX < -15)
    	{
    		player.posX = -15;
    	}
    	else if (player.posX > mapWidth - 50)
    	{
    		player.posX = mapWidth - 50;
    	}
    	if (player.posY < 5)
    	{
    		player.posY = 5;
    	}
    	else if (player.posY > mapHeight - 55)
    	{
    		player.posY = mapHeight - 55;
    	}
    	if (smallWidth)
    	{
    		player.drawPosX = player.posX + (winX - mapWidth/2);
    	}
    	if (smallHeight)
    	{
    		player.drawPosY = player.posY + (winY - mapHeight/2);
    	}
    	boolean enclosed = true; 
    	while (enclosed == true)
    	{
    		int x = (int) (player.posX - 15)/TILE_WIDTH;
        	int y = (int) (player.posY)/TILE_HEIGHT;
        	if (!mapTiles[y][x].isPassable())
        	{
        		x +=TILE_WIDTH;
        		y +=TILE_HEIGHT;
        	}
        	else
        	{
        		enclosed = false;
        	}
    	}
    }
    
    public void updatePosX(float movement)
    {
    	if (smallWidth == false)
    	{
    		if (player.posX < winX )
    		{
    			mapPosX = 0;
    		}
    		else if (player.posX > winX)
    		{
    			mapPosX = (int)player.posX - winX;
    		}
    	
    		if (player.posX + winX > mapWidth)
    		{
    			mapPosX = mapWidth - 2*winX;
    		}
    	

    	
    		mapMoveLeft = (mapPosX == 0) ? false : true;
    		mapMoveRight = (mapPosX == (mapWidth - 2*winX)) ? false : true;
    	
    		if (mapMoveLeft == true && mapMoveRight == true)
    		{
    			player.drawPosX = 400;
    		}
    		else
    		{
    			if (mapMoveLeft == false || mapMoveRight == false)
    			{
    				player.drawPosX += movement;
    			}
    		}
    	}
    	else
    	{
    		player.drawPosX += movement;
    	}
    }
    
    public void updatePosY(float movement)
    { 	
    	if (smallHeight == false) 
    	{
    		if (player.posY < winY)
    		{
    			mapPosY = 0;
    		}
    		else if (player.posY > winY)
    		{
    			mapPosY = (int)player.posY - winY;
    		}
    		if (player.posY + winY > mapHeight )
    		{
    		mapPosY = mapHeight - 2*winY;
    		}
    		
    		mapMoveDown = (mapPosY == 0) ? false : true;
    		mapMoveUp = (mapPosY == (mapHeight - 2*winY)) ? false : true;
    	
    		if (mapMoveUp == true && mapMoveDown == true)
    		{
    			player.drawPosY = 240;
    		}
    		else if (mapMoveUp == false || mapMoveDown == false)
    		{
    				player.drawPosY += movement;
    		}  		
    	}
    	else
		{
			player.drawPosY += movement;
		}
    }
    
    
    /////////////////////
    // player movement //
    /////////////////////
    
    public boolean moveLeft() throws Exception
    {
    	boolean success = false;
    	float deltaX = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.LEFT, deltaX) && player.posX > - player.getLeft())
		{
			player.posX -= deltaX;
			success = true;
			
			updatePosX(-deltaX);	
		}	
    	return success;
    }
    
    public boolean moveRight() throws Exception
    {

    	boolean success = false;
    	float deltaX = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.RIGHT, deltaX) && player.posX < mapWidth - 50)
		{
			player.posX += deltaX;
			success = true;
			updatePosX(deltaX);
		}
    	return success;
    }
    public boolean moveUp() throws Exception
    {
    	boolean success = false;
    	float deltaY = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.UP, deltaY) && player.posY < mapHeight - player.getTop())
		{
			player.posY += deltaY;
			success = true;
			updatePosY(deltaY);
		}    	    
    	return success;
    }
    public boolean moveDown() throws Exception
    {
    	boolean success = false; 
    	float deltaY = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.DOWN, deltaY) && player.posY > 0)
		{
			player.posY -= deltaY;
			success = true;
			updatePosY(-deltaY);
		}	
    	return success;
    }
    

    public boolean collides(Direction direction, float speed) throws Exception
    {
    	//////////////////////////////////////////////////
    	//MAP IS INDEXED WITH (0,0) IN TOP LEFT         //
    	//CHAR POS STARTS WITH (0,0) IN BOTTOM LEFT     //
    	//////////////////////////////////////////////////
    	double playerLeftSide = player.posX + player.getLeft();
    	double playerRightSide = player.posX + player.getRight();
    	double playerBottomSide = player.posY + player.getBottom();
    	double playerTopSide = player.posY + player.getTop();

        double tilesLeftSide;
        double tilesRightSide;
        double tilesTopSide;
        double tilesBottomSide;

        int row0, col0, row1, col1;
        
        Rectangle futurePlayerRect;
        Rectangle tilesRect;
        //calculate requested new position of character and the rows and cols of tiles to check for collision
        if (Direction.LEFT == direction)
        {
            int tilesToLeftXIndex = ((int) playerLeftSide / TILE_WIDTH) - 1;
            int bottomTileToLeftYIndex = (int) playerBottomSide / TILE_HEIGHT;
            int topTileToLeftYIndex = (int) playerTopSide / TILE_HEIGHT;
            
            tilesRightSide = tilesToLeftXIndex * TILE_WIDTH + TILE_WIDTH;
            tilesLeftSide = tilesToLeftXIndex * TILE_WIDTH;
            tilesTopSide = topTileToLeftYIndex * TILE_HEIGHT + TILE_HEIGHT - 1;
            tilesBottomSide = bottomTileToLeftYIndex * TILE_HEIGHT;

            futurePlayerRect = new Rectangle(playerLeftSide - speed, playerRightSide - speed, playerTopSide, playerBottomSide);
            
            row0 = bottomTileToLeftYIndex;
            col0 = tilesToLeftXIndex;
            row1 = topTileToLeftYIndex;
            col1 = col0;
            
            
            
        } else if(Direction.RIGHT == direction) {
        	int tilesToRightXIndex = ((int) playerRightSide / TILE_WIDTH) + 1;
        	int bottomTileToLeftYIndex = (int) playerBottomSide / TILE_HEIGHT;
            int topTileToLeftYIndex = (int) playerTopSide / TILE_HEIGHT;
            
            tilesRightSide = tilesToRightXIndex * TILE_WIDTH + TILE_WIDTH;
            tilesLeftSide = tilesToRightXIndex * TILE_WIDTH;
            tilesTopSide = topTileToLeftYIndex * TILE_HEIGHT + TILE_HEIGHT - 1;
            tilesBottomSide = bottomTileToLeftYIndex * TILE_HEIGHT;
            
            futurePlayerRect = new Rectangle(playerLeftSide + speed, playerRightSide + speed, playerTopSide, playerBottomSide);
            
            row0 = bottomTileToLeftYIndex;
            col0 = tilesToRightXIndex;
            row1 = topTileToLeftYIndex;
            col1 = col0;
            
            
        } else if (Direction.UP == direction) {
        	int tileToLeftXIndex = ((int) playerLeftSide / TILE_WIDTH);
        	int tileToRightXIndex = ((int) playerRightSide / TILE_WIDTH);
        	int tilesYIndex = ((int) playerTopSide / TILE_HEIGHT + 1);
        	
        	tilesRightSide = tileToRightXIndex * TILE_WIDTH + TILE_WIDTH;
            tilesLeftSide = tileToLeftXIndex * TILE_WIDTH;
            tilesTopSide = tilesYIndex * TILE_HEIGHT + TILE_HEIGHT - 1;
            tilesBottomSide = tilesYIndex * TILE_HEIGHT;
            
            futurePlayerRect = new Rectangle(playerLeftSide, playerRightSide, playerTopSide + speed, playerBottomSide + speed);
            
            row0 = tilesYIndex;
            col0 = tileToLeftXIndex;
            row1 = row0;
            col1 = tileToRightXIndex;
            		
            
        } else if (Direction.DOWN == direction) {
        	int tileToLeftXIndex = ((int) playerLeftSide / TILE_WIDTH);
        	int tileToRightXIndex = ((int) playerRightSide / TILE_WIDTH);
        	int tilesYIndex = ((int) playerTopSide / TILE_HEIGHT - 1);
        	
        	tilesRightSide = tileToRightXIndex * TILE_WIDTH + TILE_WIDTH;
            tilesLeftSide = tileToLeftXIndex * TILE_WIDTH;
            tilesTopSide = tilesYIndex * TILE_HEIGHT + TILE_HEIGHT - 1;
            tilesBottomSide = tilesYIndex * TILE_HEIGHT;
            
            futurePlayerRect = new Rectangle(playerLeftSide, playerRightSide, playerTopSide + speed, playerBottomSide + speed);
            
            row0 = tilesYIndex;
            col0 = tileToLeftXIndex;
            row1 = row0;
            col1 = tileToRightXIndex;
        } else {
        	throw new Exception("invalid Direction");
        }
        	
        	
        	
        	
        tilesRect = new Rectangle(tilesLeftSide, tilesRightSide, tilesTopSide, tilesBottomSide);
        //check for collision of tiles in Direction.x are not passable
        //System.out.println("row 0: " + row0 + "col 0: " + col0 + "row 1: " + row1 + "col1: " + col1);
        //first part of if statement avoids giving bothPassable args which would cause IndexOutOfBounds exception
        if (!(row0 < 0 || col0 < 0 || row1 < 0 || col1 < 0 || row0 >= mapHeight/TILE_HEIGHT || row1 >= mapHeight/TILE_HEIGHT || col0 >= mapWidth/TILE_WIDTH || col1 >= mapWidth/TILE_WIDTH) && !bothPassable(row0, col0, row1, col1)) {
        	return rectIntersect(futurePlayerRect, tilesRect);
        }
        return false;
    }
    
    
    
    
    private boolean bothPassable(int row0, int col0, int row1, int col1) {
    	System.out.println(row0 + ", " + col0 + " || " + row1 + ", " + col1 + " || ");
    	
    	return mapTiles[bottomLeftIndexedRowToTopLeftIndexedRow(row0)][col0].isPassable()
        		&& mapTiles[bottomLeftIndexedRowToTopLeftIndexedRow(row1)][col1].isPassable();
    }
    private int bottomLeftIndexedRowToTopLeftIndexedRow(int row) {
    	return this.numRows - 1 - row;
    }
    private boolean rectIntersect(Rectangle a, Rectangle b) {
    	return (a.left <= b.right &&
    			b.left <= a.right &&
    		    a.top >= b.bottom &&
    		    b.top >= a.bottom);
    }
}
