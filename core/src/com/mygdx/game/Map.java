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
    
    
    private float charPosX = 2000;
    private float charPosY = 1100;
    private float charDrawPosX = 0;
    private float charDrawPosY = 0;
    private int mapPosX;
    private int mapPosY;
    private int sightX;
    private int sightY;
    private int winX = 400;
    private int winY = 240;
    
    
    Texture mapImage;
    TextureRegion fov;
    
    
    
    public Map(String mapFile, String tileFile) throws IOException
    {
        ///////////////////////////////////
        // convert mapFile into Tile[][] //
        ///////////////////////////////////
        Scanner mapFileScanner = new Scanner(new File(mapFile));
        title = mapFileScanner.nextLine();
        numRows = Integer.parseInt(mapFileScanner.nextLine());
        numCols = Integer.parseInt(mapFileScanner.nextLine());
        mapTiles = new Tile[numRows][numCols];
        
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
        
        int itemIndex = Integer.parseInt(mapFileScanner.nextLine());
        if (itemIndex > 0)
        {
        	
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
    public void setFOV(int x, int y)
    {
        sightX = x;
        sightY = y;
    }
    
    public void update()
    {
    	//The setRegion uses the top left corner of the map as a starting point. 
    	//All numbers in the y direction go from bottom to top in all other functions, but the final value is inverted within the the below function for proper usage.
        fov.setRegion(mapPosX, mapHeight - mapPosY, 2*winX, 2*winY); 
    }
    
    public void draw(SpriteBatch batch)
    {
        batch.draw(fov, 0, 0);
    }
    
    public float getCharDrawPosX()
    {
    	return charDrawPosX;
    }
    public float getCharDrawPosY()
    {
    	return charDrawPosY;
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
    	
    	if (charPosX < winX && smallWidth == false)
    	{
    		charDrawPosX = charPosX;
    	}
    	else if (charPosX > mapWidth - winX && smallWidth == false)
    	{
    		charDrawPosX = (charPosX - mapWidth + 2*winX);
    	}
    	
    	
    	if (charPosY < winY && smallHeight == false)
    	{
    		charDrawPosY = charPosY;
    	}
    	else if (charPosY > mapHeight - winY && smallHeight == false)
    	{
    		charDrawPosY = (charPosY - (mapHeight - winY) + winY);
    	}
    	if (smallWidth == true)
    	{
    		mapPosX = mapWidth/2 - winX;
    	}
    	if (smallHeight == true)
    	{
    		mapPosY = mapHeight/2 + winY;
    	}
    }
    
    
    public void adjustCharPlacement()
    {
    	if (charPosX < -15)
    	{
    		charPosX = -15;
    	}
    	else if (charPosX > mapWidth - 50)
    	{
    		charPosX = mapWidth - 50;
    	}
    	if (charPosY < 5)
    	{
    		charPosY = 5;
    	}
    	else if (charPosY > mapHeight - 55)
    	{
    		charPosY = mapHeight - 55;
    	}
    	if (smallWidth)
    	{
    		charDrawPosX = charPosX + (winX - mapWidth/2);
    	}
    	if (smallHeight)
    	{
    		charDrawPosY = charPosY + (winY - mapHeight/2);
    	}
    	boolean enclosed = true; 
    	while (enclosed == true)
    	{
    		int x = (int) (charPosX - 15)/TILE_WIDTH;
        	int y = (int) (charPosY)/TILE_HEIGHT;
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
    		if (charPosX - winX < 0)
    		{
    			mapPosX = 0;
    		}
    		else if (charPosX - winX > 0)
    		{
    			mapPosX = (int)charPosX - winX;
    		}
    	
    		if (charPosX + winX > mapWidth)
    		{
    			mapPosX = mapWidth - 2*winX;
    		}
    	

    	
    		mapMoveLeft = (mapPosX == 0) ? false : true;
    		mapMoveRight = (mapPosX == (mapWidth - 2*winX)) ? false : true;
    	
    		if (mapMoveLeft == true && mapMoveRight == true)
    		{
    			charDrawPosX = 400;
    		}
    		else
    		{
    			if (mapMoveLeft == false || mapMoveRight == false)
    			{
    				charDrawPosX += movement;
    			}
    		}
    	}
    	else
    	{
    		charDrawPosX += movement;
    	}
    }
    
    public void updatePosY(float movement)
    { 	
    	if (smallHeight == false) 
    	{
    		if (charPosY < winY)
    		{
    			mapPosY = 2*winY;
    		}
    		else if (mapHeight - (charPosY + winY) > 0)
    		{
    			mapPosY = (int)(charPosY + winY);
    		}
    		if (mapHeight < charPosY + winY)
    		{
    		mapPosY = mapHeight;
    		}
    		
    		mapMoveUp = (mapPosY == (mapHeight)) ? false : true;
    		mapMoveDown = (mapPosY == 2*winY) ? false : true;
    	
    		if (mapMoveUp == true && mapMoveDown == true)
    		{
    			charDrawPosY = 240;
    		}
    		else if (mapMoveUp == false || mapMoveDown == false)
    		{
    				charDrawPosY += movement;
    		}  		
    	}
    	else
		{
			charDrawPosY += movement;
		}
    }
    
    
    /////////////////////
    // player movement //
    /////////////////////
    
    public boolean moveLeft(Entity entity)
    {
    	boolean success = false;
    	float deltaX = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.LEFT, deltaX, entity) && charPosX> - entity.getLeft())
		{
			charPosX -= deltaX;
			success = true;
			
			updatePosX(-deltaX);	
		}	
    	return success;
    }
    
    public boolean moveRight(Entity entity)
    {

    	boolean success = false;
    	float deltaX = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.RIGHT, deltaX, entity) && charPosX < mapWidth - 50)
		{
			charPosX += deltaX;
			success = true;
			updatePosX(deltaX);
		}
    	return success;
    }
    public boolean moveUp(Entity entity)
    {
    	boolean success = false;
    	float deltaY = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.UP, deltaY, entity) && charPosY < mapHeight - entity.getTop())
		{
			charPosY += deltaY;
			success = true;
			updatePosY(deltaY);
		}    	    
    	return success;
    }
    public boolean moveDown(Entity entity)
    {
    	boolean success = false; 
    	float deltaY = 200 * Gdx.graphics.getDeltaTime();
		if (!collides(Direction.DOWN, deltaY, entity) && charPosY > 0)
		{
			charPosY -= deltaY;
			success = true;
			updatePosY(-deltaY);
		}	
    	return success;
    }
    
    public boolean collides(Direction direction, float speed, Entity entity)
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
            x1 = charPosX + entity.getLeft(); //right side of hitbox relative to bottom left corner of image of current frame kek
            y1 = charPosY + entity.getBottom(); //bottom side of hitbox
            
            //top left corner
            
            y2 = (charPosY + entity.getTop() <= mapHeight) ? (charPosY + entity.getTop()) : mapHeight - 2;
            
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
    	    x1 = charPosX + entity.getRight(); //right side of hitbox relative to bottom left corner of image of current frame kek
            y1 = charPosY + entity.getBottom(); //bottom side of hitbox
          
            //top left corner
          
            y2 = (charPosY + entity.getTop() <= mapHeight) ? (charPosY + entity.getTop()) : mapWidth - 2;
            
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
            
            x1 = charPosX + entity.getLeft();
            y1 = (charPosY + entity.getTop() <= mapHeight) ? (charPosY + entity.getTop()): mapHeight - 2;
            
            x2 = (charPosX + entity.getRight() <= mapWidth) ? (charPosX + entity.getRight()): mapWidth;
           

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
            
            System.out.println("Moving Up");
            if (ignore == "none") {
            	
            	System.out.println(mapTiles[tileAboveY][tileAboveX1].getName() +", " + mapTiles[tileAboveY][tileAboveX2].getName());
            	
            }
            ///////////print statements//////////////////
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("y1:  " + y1 + "    x1: " + x1 + "    x2: " + x2);
            System.out.println("tile above Y: " + tileAboveY + "   tileAboveX1: " + tileAboveX1 + "  tileAboveX2:  " + tileAboveX2);
            ////////////////////////////////////////////
            //handles both corners
            //a or (b and c) = (a or b) and (a or c) 
            if (ignore == "none" && tileAboveY > -1  && ((mapTiles[tileAboveY][tileAboveX1].hasBottomWall()) || (mapTiles[tileAboveY][tileAboveX2].hasBottomWall()))) {
                int tileAboveWallY = unconvertedTileAboveY * TILE_HEIGHT;
                System.out.println("tileAboveWallY: " + tileAboveWallY);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 + speed >= tileAboveWallY ) {
                    return true;
                }
            }
            else if (ignore == "right" && tileAboveY > -1 && (mapTiles[tileAboveY][tileAboveX1].hasBottomWall())) {
                int tileAboveWallY = unconvertedTileAboveY * TILE_HEIGHT;
                System.out.println("tileAboveWallY: " + tileAboveWallY);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 + speed >= tileAboveWallY ) {
                    return true;
                }
            }
            else if (ignore == "left" && tileAboveY > -1 && (mapTiles[tileAboveY][tileAboveX2].hasBottomWall())) {
                int tileAboveWallY = unconvertedTileAboveY * TILE_HEIGHT;
                System.out.println("tileAboveWallY: " + tileAboveWallY);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 + speed >= tileAboveWallY ) {
                    return true;
                }
            }
            return false;

        }
        else //(Direction.DOWN == direction)
        {
        	//bottom  left and bottom right corners
            x1 = charPosX + entity.getLeft();
            y1 = (charPosY + entity.getBottom() <= mapHeight) ? (charPosY + entity.getBottom()): mapHeight - 2;
            
            x2 = (charPosX + entity.getRight() <= mapWidth) ? (charPosX + entity.getRight()): mapWidth;
           

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
            
            System.out.println("Moving NOT (UP or left or right)");
            if (ignore == "none") 
            {
            	
            	System.out.println(mapTiles[tileBelowY][tileBelowX1].getName() +", " + mapTiles[tileBelowY][tileBelowX2].getName());
            	
            }
            ///////////print statements//////////////////
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("y1:  " + y1 + "    x1: " + x1 + "    x2: " + x2);
            System.out.println("tile above Y: " + tileBelowY + "   tileBelowX1: " + tileBelowX1 + "  tileBelowX2:  " + tileBelowX2);
            ////////////////////////////////////////////
            //handles both corners
            //a or (b and c) = (a or b) and (a or c) 
            if (ignore == "none" && tileBelowY > -1 && ((mapTiles[tileBelowY][tileBelowX2].hasTopWall()) || (mapTiles[tileBelowY][tileBelowX1].hasTopWall())))
            {
            	int tileBelowWallY = unconvertedTileBelowY * TILE_HEIGHT + TILE_HEIGHT - 1;
                System.out.println("tileBelowWallY: " + tileBelowWallY);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 - speed <= tileBelowWallY + 1) {
                    return true;
                }
            }
            else if (ignore == "right" && tileBelowY > -1 &&  (mapTiles[tileBelowY][tileBelowX1].hasTopWall())) 
            {
            	int tileBelowWallY = unconvertedTileBelowY * TILE_HEIGHT + TILE_HEIGHT - 1;
                System.out.println("tileBelowWallY: " + tileBelowWallY);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if (y1 - speed <= tileBelowWallY + 1) {
                    return true;
                }
            }
            else if (ignore == "left" && tileBelowY > -1 && (mapTiles[tileBelowY][tileBelowX2].hasTopWall()))
            {
            	int tileBelowWallY = unconvertedTileBelowY * TILE_HEIGHT + TILE_HEIGHT - 1;
                System.out.println("tileBelowWallY: " + tileBelowWallY);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
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
