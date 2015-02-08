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
    private int row;
    private int col;
    private static final int TILE_WIDTH = 80;
    private static final int TILE_HEIGHT = 80;
    private int[][] mapTiles;
    
    private int charPosX = 0;
    private int charPosY = 0;
    private int sightX;
    private int sightY;
    
    
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
        
        fov = new TextureRegion(mapImage, 0, 0, 2 * sightX, 2 * sightY);
        sc.close();
    }
    
    
    //updating and drawing the visible part of the map
    public void setFOV(int x, int y)
    {
        sightX = x;
        sightY = y;
    }
    
    public void update(float x, float y)
    {
        charPosX = Math.round(x);
        charPosY = Math.round(y);
        fov.setRegion(charPosX - sightX, charPosY - sightY, 2*sightX, 2*sightY); 
    }
    
    public void draw(SpriteBatch batch)
    {
        batch.draw(fov, 0, 0);
    }

}
