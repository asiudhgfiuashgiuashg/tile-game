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
    private int[][] mapTiles;
    private int mapWidth;
    private int mapHeight;
    private boolean mapMoveLeft;
    private boolean mapMoveRight;
    private boolean mapMoveUp;
    private boolean mapMoveDown;
    
    
    private float charPosX = 1100;
    private float charPosY = 150;
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
    
    
    public Map(String mapFile) throws IOException
    {
        ///////////////////////////////////
        // convert mapFile into Tile[][] //
        ///////////////////////////////////
        Scanner sc = new Scanner(new File(mapFile));
        title = sc.nextLine();
        row = Integer.parseInt(sc.nextLine());
        col = Integer.parseInt(sc.nextLine());
        mapTiles = new int[row][col];
        for (int r = 0; r < row; r++)
        {
            String line = sc.nextLine();
            String[] nums = line.split("\\s+");
            for (int c = 0; c < nums.length; c++)
            {
                mapTiles[r][c] = Integer.parseInt(nums[c]);
            }
        }
        
        ///////////////////////////////////////////////////////////
        // create and save png which is composite of tile images //
        ///////////////////////////////////////////////////////////
        BufferedImage bigImage = new BufferedImage(TILE_WIDTH * col, TILE_HEIGHT * row, BufferedImage.TYPE_INT_ARGB); //made up of combined tiles
        Graphics g = bigImage.getGraphics();
        for (int r = 0; r < row; r++)
        {
            for (int c = 0; c < col; c++)
            {
                BufferedImage currTileImg;
                if (mapTiles[r][c] == 0)
                {
                    currTileImg = ImageIO.read(new File("../core/assets/whiteSquare.png"));
                } else //1
                {
                    currTileImg = ImageIO.read(new File("../core/assets/blackSquare.png"));
                }
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
        sc.close();
    }
    
    
    //updating and drawing the visible part of the map
    public void setFOV(int x, int y)
    {
        sightX = x;
        sightY = y;
    }
    
    public void update()
    {
        fov.setRegion(mapPosX, mapPosY, 2*winX, 2*winY); 
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
    		charDrawPosX = (charPosX -(mapWidth - winX) + winX);
    	}
    	
    	
    	if (charPosY < winY)
    	{
    		charDrawPosY = charPosY;
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
    	mapMoveUp = (mapPosY == 0) ? false : true;
    	mapMoveDown = (mapPosY == (mapHeight - 2*winY)) ? false : true;
    	
    	
    	if (mapHeight < charPosY + winY)
    	{
    		mapPosY = 0;
    	}
    	else if (mapHeight - (charPosY + winY) > 0)
    	{
    		mapPosY = mapHeight - (int)(charPosY + winY);
    	}
    	if (charPosY < winY)
    	{
    		mapPosY = mapHeight - 2*winY;
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
