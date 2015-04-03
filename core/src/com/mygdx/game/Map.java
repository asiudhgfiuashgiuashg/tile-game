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
import com.badlogic.gdx.Input.Keys;
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
        		String[] currentAttributes = new String[0];
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
        		boolean leftWall = Boolean.parseBoolean(currentAttributes[3]);
        		boolean rightWall = Boolean.parseBoolean(currentAttributes[4]);
        		boolean topWall = Boolean.parseBoolean(currentAttributes[5]);
        		boolean bottomWall = Boolean.parseBoolean(currentAttributes[6]);
        		//need to also get hazard once class Item is implemented
        		Tile newTile = new Tile(imageURI, name, leftWall, rightWall, topWall, bottomWall);
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
        System.out.println(mapPosX + ", " + (mapPosY));
        
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
        	if (mapTiles[y][x].hasLeftWall() && mapTiles[y][x].hasRightWall() && mapTiles[y][x].hasTopWall() && mapTiles[y][x].hasBottomWall())
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
    
    public boolean moveLeft()
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
    
    public boolean moveRight()
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
    public boolean moveUp()
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
    public boolean moveDown()
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
    
    public boolean collides(Direction direction, float speed)
    {
    	//////////////////////////////////////////////////
    	//MAP IS INDEXED WITH (0,0) IN TOP LEFT         //
    	//CHAR POS STARTS WITH (0,0) IN BOTTOM LEFT     //
    	/////////////////////////////////////////////////
        float x1;
        float y1;
        float x2;
        float y2;
        
        if (Direction.LEFT == direction)
        {
        	
            //bottom left and top left corners
            //bottom left corner
            x1 = player.posX + player.getLeft(); //right side of hitbox relative to bottom left corner of image of current frame kek
            y1 = player.posY + player.getBottom(); //bottom side of hitbox
            
            //top left corner
            
            y2 = (player.posY + player.getTop() <= mapHeight) ? (player.posY + player.getTop()) : mapHeight - 2;
            
            //in the tile grid
            int tileToLeftX = ((int) x1 / TILE_WIDTH) - 1;
            int tileToLeftY1 = bottomLeftIndexedRowToTopLeftIndexedRow(((int) y1 / TILE_HEIGHT));
            int tileToLeftY2 = bottomLeftIndexedRowToTopLeftIndexedRow(((int) y2 / TILE_HEIGHT));
            
/*            System.out.println("Moving Left");*/
/*            if (tileToLeftY1 > 0 && tileToLeftX > 0) {

            	
            	System.out.println(mapTiles[tileToLeftY2][tileToLeftX].getName());
            	System.out.println(mapTiles[tileToLeftY1][tileToLeftX].getName());
            	
            }*/
/*            ///////////print statements//////////////////
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("x1:  " + x1 + "    y1: " + y1 + "    y2: " + y2);
            System.out.println("tile to left X: " + tileToLeftX + "   tileToLeftY1: " + tileToLeftY1 + "  tileToLeftY2:  " + tileToLeftY2);

            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            ////////////////////////////////////////////
*/
            //handles both corners
            //a or (b and c) = (a or b) and (a or c) 
            if ((tileToLeftX > -1 && tileToLeftY1 > -1 && tileToLeftY2 > -1) && ((mapTiles[tileToLeftY1][tileToLeftX].hasRightWall()) || (mapTiles[tileToLeftY2][tileToLeftX].hasRightWall()))) {
                int tileToLeftWallX = tileToLeftX * TILE_WIDTH + TILE_WIDTH - 1;
                if (x1 - speed <= tileToLeftWallX + 1) {
                    return true;
                }
            }
            return false;
        }
        else if (Direction.RIGHT == direction)
        {
            //bottom and top right corners
    	    x1 = player.posX + player.getRight(); //right side of hitbox relative to bottom left corner of image of current frame kek
            y1 = player.posY + player.getBottom(); //bottom side of hitbox
          
            //top left corner
          
            y2 = (player.posY + player.getTop() <= mapHeight) ? (player.posY + player.getTop()) : mapWidth - 2;
            
            //in the tile grid
            int tileToRightX = (((int) x1 / TILE_WIDTH) < numCols) ? ((int) x1 / TILE_WIDTH): numCols -1;
            
            if (((int) x1 / TILE_WIDTH) < numCols)
            {
            	tileToRightX = ((int) x1 / TILE_WIDTH);
            }
            else
            {
            	return false;
            }
            
            int tileToRightY1 = bottomLeftIndexedRowToTopLeftIndexedRow(((int) y1 / TILE_HEIGHT));
            int tileToRightY2 = bottomLeftIndexedRowToTopLeftIndexedRow(((int) y2 / TILE_HEIGHT));
            
            if ((tileToRightX > -1 && tileToRightY1 > -1 && tileToRightY2 > -1 ) && ((mapTiles[tileToRightY1][tileToRightX].hasLeftWall()) || (mapTiles[tileToRightY2][tileToRightX].hasLeftWall()))) {
                int tileToRightWallX = tileToRightX * TILE_WIDTH;
                if (x1 + speed >= tileToRightWallX) {
                    return true;
                }
            }
            return false;

        }
        else if (Direction.UP == direction)
        {
            //top left and top right corners
            
            x1 = player.posX + player.getLeft();
            y1 = (player.posY + player.getTop() <= mapHeight) ? (player.posY + player.getTop()): mapHeight - 2;
            
            x2 = (player.posX + player.getRight() <= mapWidth) ? (player.posX + player.getRight()): mapWidth;
           

            int unconvertedTileAboveY =  ((int) y1 / TILE_HEIGHT) + 1;

            int tileAboveY;
            if (bottomLeftIndexedRowToTopLeftIndexedRow(((int) y1 / TILE_HEIGHT) + 1) > -1)
            	{
            	tileAboveY = bottomLeftIndexedRowToTopLeftIndexedRow(((int) y1 / TILE_HEIGHT) + 1);
            	}
            else
            {
            	return false;
            }
            int tileAboveX1 = ((int) x1 / TILE_WIDTH);
            int tileAboveX2 = ((int) x2 / TILE_WIDTH);
            
            String ignore = "none";
            if (tileAboveX1 < 0)
            {
            	ignore = "left";
            }
            else if (tileAboveX2 > numCols-1)
            {
            	ignore = "right";
            }
            
            //System.out.println("Moving Up");
            /*if (ignore == "none") {
            	
            	System.out.println(mapTiles[tileAboveY][tileAboveX1].getName() +", " + mapTiles[tileAboveY][tileAboveX2].getName());
            	
            }
            ///////////print statements//////////////////
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("y1:  " + y1 + "    x1: " + x1 + "    x2: " + x2);
            System.out.println("tile above Y: " + tileAboveY + "   tileAboveX1: " + tileAboveX1 + "  tileAboveX2:  " + tileAboveX2);
            ////////////////////////////////////////////*/
            //handles both corners
            //a or (b and c) = (a or b) and (a or c) 
            if (ignore == "none" && tileAboveY > -1  && ((mapTiles[tileAboveY][tileAboveX1].hasBottomWall()) || (mapTiles[tileAboveY][tileAboveX2].hasBottomWall()))) {
                int tileAboveWallY = unconvertedTileAboveY * TILE_HEIGHT;
                //System.out.println("tileAboveWallY: " + tileAboveWallY);
                //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 + speed >= tileAboveWallY ) {
                    return true;
                }
            }
            else if (ignore == "right" && tileAboveY > -1 && (mapTiles[tileAboveY][tileAboveX1].hasBottomWall())) {
                int tileAboveWallY = unconvertedTileAboveY * TILE_HEIGHT;
                //System.out.println("tileAboveWallY: " + tileAboveWallY);
                //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 + speed >= tileAboveWallY ) {
                    return true;
                }
            }
            else if (ignore == "left" && tileAboveY > -1 && (mapTiles[tileAboveY][tileAboveX2].hasBottomWall())) {
                int tileAboveWallY = unconvertedTileAboveY * TILE_HEIGHT;
                //System.out.println("tileAboveWallY: " + tileAboveWallY);
                //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 + speed >= tileAboveWallY ) {
                    return true;
                }
            }
            return false;

        }
        else //(Direction.DOWN == direction)
        {
        	//bottom  left and bottom right corners
            x1 = player.posX + player.getLeft();
            y1 = (player.posY + player.getBottom() <= mapHeight) ? (player.posY + player.getBottom()): mapHeight - 2;
            
            x2 = (player.posX + player.getRight() <= mapWidth) ? (player.posX + player.getRight()): mapWidth;
           

            int unconvertedTileBelowY =  ((int) y1 / TILE_HEIGHT) - 1;
            int tileBelowY;
            if (bottomLeftIndexedRowToTopLeftIndexedRow(unconvertedTileBelowY) < numRows)
            {
            	tileBelowY = bottomLeftIndexedRowToTopLeftIndexedRow(unconvertedTileBelowY);
            }
            else
            {
            	return false;
            }
            String ignore = "none";
            int tileBelowX1 = ((int) x1 / TILE_WIDTH);
            int tileBelowX2 = ((int) x2 / TILE_WIDTH);
            if (tileBelowX1 < 0)
            {
            	ignore = "left";
            }
            else if (tileBelowX2 > numCols-1)
            {
            	ignore = "right";
            }
            
            //System.out.println("Moving NOT (UP or left or right)");
            /*if (ignore == "none") 
            {
            	
            	System.out.println(mapTiles[tileBelowY][tileBelowX1].getName() +", " + mapTiles[tileBelowY][tileBelowX2].getName());
            	
            }
            ///////////print statements//////////////////
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("y1:  " + y1 + "    x1: " + x1 + "    x2: " + x2);
            System.out.println("tile above Y: " + tileBelowY + "   tileBelowX1: " + tileBelowX1 + "  tileBelowX2:  " + tileBelowX2);
            ////////////////////////////////////////////*/
            //handles both corners
            //a or (b and c) = (a or b) and (a or c) 
            if (ignore == "none" && tileBelowY > -1 && ((mapTiles[tileBelowY][tileBelowX2].hasTopWall()) || (mapTiles[tileBelowY][tileBelowX1].hasTopWall())))
            {
            	int tileBelowWallY = unconvertedTileBelowY * TILE_HEIGHT + TILE_HEIGHT - 1;
                //System.out.println("tileBelowWallY: " + tileBelowWallY);
                //.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 - speed <= tileBelowWallY + 1) {
                    return true;
                }
            }
            else if (ignore == "right" && tileBelowY > -1 &&  (mapTiles[tileBelowY][tileBelowX1].hasTopWall())) 
            {
            	int tileBelowWallY = unconvertedTileBelowY * TILE_HEIGHT + TILE_HEIGHT - 1;
                //System.out.println("tileBelowWallY: " + tileBelowWallY);
                //.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 - speed <= tileBelowWallY + 1) {
                    return true;
                }
            }
            else if (ignore == "left" && tileBelowY > -1 && (mapTiles[tileBelowY][tileBelowX2].hasTopWall()))
            {
            	int tileBelowWallY = unconvertedTileBelowY * TILE_HEIGHT + TILE_HEIGHT - 1;
                //System.out.println("tileBelowWallY: " + tileBelowWallY);
                //System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 - speed <= tileBelowWallY + 1) {
                    return true;
                }
            }
            return false;
        }
        //System.out.println("welp, you done fucked up now! no valid directions");
        //return false;
    }
    private int bottomLeftIndexedRowToTopLeftIndexedRow(int row) {
    	return this.numRows - 1 - row;
    }
}
