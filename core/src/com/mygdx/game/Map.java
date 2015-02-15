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
    private int row;
    private int col;
    private static final int TILE_WIDTH = 80;
    private static final int TILE_HEIGHT = 80;
    private Tile[][] mapTiles;
    private int mapWidth;
    private int mapHeight;
    private boolean mapMoveLeft;
    private boolean mapMoveRight;
    private boolean mapMoveUp;
    private boolean mapMoveDown;
    
    
    private float charPosX = 200;
    private float charPosY = 860;
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
        row = Integer.parseInt(mapFileScanner.nextLine());
        col = Integer.parseInt(mapFileScanner.nextLine());
        mapTiles = new Tile[row][col];
        
        Scanner tileFileScanner = new Scanner(new File(tileFile));
        for (int r = 0; r < row; r++) {
            String currentRow = mapFileScanner.nextLine();
            String[] individualIds = currentRow.split("\\s+");
            System.out.println(currentRow);
            System.out.println("col is: " + col);
        	for (int c = 0; c < col; c++/*ha*/) {
        	    System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        		System.out.println(c);
        		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        		String individualTileId = individualIds[c];
        		//find corresponding line in tileFile
        		String currentTilesLine = null;
        		String[] currentAttributes = new String[0];
        		boolean found = false;
        		while (!found)
        		{
        			currentTilesLine = tileFileScanner.nextLine();
        			currentAttributes = currentTilesLine.split(", ");
        			System.out.println(" :::: " + currentAttributes[1] + " / " + individualTileId);
        			System.out.print(currentTilesLine);
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
        mapFileScanner.close();
        ///////////////////////////////////////////////////////////
        // create and save png which is composite of tile images //
        ///////////////////////////////////////////////////////////
        System.out.println("CREATING COMPOSITE MAP IMAGE");
        BufferedImage bigImage = new BufferedImage(TILE_WIDTH * col, TILE_HEIGHT * row, BufferedImage.TYPE_INT_ARGB); //made up of combined tiles
        Graphics g = bigImage.getGraphics();
        for (int r = 0; r < row; r++)
        {
            for (int c = 0; c < col; c++)
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
        mapWidth = TILE_WIDTH * col;
        mapHeight = TILE_HEIGHT * row;
        
        initialCharPos();
        
       updatePosX(0);
       updatePosY(0);
            
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
    	if (charPosX < winX)
    	{
    		charDrawPosX = charPosX;
    	}
    	else if (charPosX > mapWidth - winX)
    	{
    		charDrawPosX = (charPosX - mapWidth + 2*winX);
    	}
    	
    	
    	if (charPosY < winY)
    	{
    		charDrawPosY = charPosY;
    	}
    	else if (charPosY > mapHeight - winY)
    	{
    		charDrawPosY = (charPosY - (mapHeight - winY) + winY);
    	}
    	
    }
    
    public void updatePosX(float movement)
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
    
    public void updatePosY(float movement)
    { 	
    	mapMoveUp = (mapPosY == (mapHeight)) ? false : true;
    	mapMoveDown = (mapPosY == 2*winY) ? false : true;
    	
    	
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
   
    	
    	if (mapMoveUp == true && mapMoveDown == true)
        {
        	charDrawPosY = 240;
        }
		else
        {
        	if (mapMoveUp == false || mapMoveDown == false)
        	{
        		charDrawPosY += movement;
        	}
        }
    	
    }
    
    
    /////////////////////
    // player movement //
    /////////////////////
    
    public boolean moveLeft()
    {
    	boolean success = false;
    	if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
    	{	
    		if (charPosX > -15)
    		{
    			charPosX -= 200 * Gdx.graphics.getDeltaTime();
    			success = true;
    			
    			updatePosX(-200 * Gdx.graphics.getDeltaTime());	
    		}	
    	}
    	return success;
    }
    
    public boolean moveRight()
    {
    	boolean success = false;
    	if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
    	{            
    		if (charPosX < mapWidth - 50)
    		{
    			charPosX += 200 * Gdx.graphics.getDeltaTime();
    			success = true;
    			updatePosX(200 * Gdx.graphics.getDeltaTime());
    		}
    		         
    	}
    	return success;
    }
    public boolean moveUp()
    {
    	boolean success = false;
    	if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
    	{         
    		if (charPosY < mapHeight -50)
    		{
    			charPosY += 200 * Gdx.graphics.getDeltaTime();
    			success = true;
    			updatePosY(200 * Gdx.graphics.getDeltaTime());
    		}
        	      
    	}
    	return success;
    }
    public boolean moveDown()
    {
    	boolean success = false;
    	if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
    	{         
    		if (charPosY > 0)
    		{
    			charPosY -= 200 * Gdx.graphics.getDeltaTime();
    			success = true;
    			updatePosY(-200 * Gdx.graphics.getDeltaTime());
    		}	
    	}
    	return success;
    }
}
