////////////////////////////////////////////////////////////////////////////////
// 
//  Project:  theGame-core
//  File:     Player.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
public class Player extends Entity
{
	
	TextureRegion idleRight;
	TextureRegion idleLeft;
	TextureRegion idleUp;
	TextureRegion idleDown;
	Map currentMap;

    @Override
    public void create()
    {
    	int sightX = 400;
    	int sightY = 240;
    	
        spriteSheet = new Texture(Gdx.files.internal("index.png"));
        tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAME_COLS, spriteSheet.getHeight() / FRAME_ROWS);
        
        moveLeft = animate(9, 9);
        idleLeft = tmp[9][0];
        moveRight = animate(11, 9);
        idleRight = tmp[11][0];
        moveUp = animate(8, 9);
        idleUp = tmp[8][0];
        moveDown = animate(10, 9);
        idleDown = tmp[10][0];
        currentFrame = idleUp;
        
        try
        {
        	System.out.println("Working Directory = " + System.getProperty("user.dir"));
        	currentMap = new Map("../core/assets/Test.txt", "../core/assets/Tiles.txt");
        	currentMap.setFOV(sightX, sightY);
        }
        catch(IOException e)
        {
        	System.out.println(e.getMessage());
        	System.out.println("Failed to create map object");
        }
    }
    
    
    protected Animation animate(int row, int length)
    {
        animationFrames = new TextureRegion[length];
        int index = 0;
        for (int j = 0; j < length; j++)
        {
            animationFrames[index++] = tmp[row][j];
        }
        Animation movement = new Animation(0.025f, animationFrames);

        return movement;
    }
    
    @Override
    public void update(float stateTime) {
    	
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
        {
            if (currentMap.moveLeft())
            {
            	currentFrame = moveLeft.getKeyFrame(stateTime, true);
            }
        }
        
        else if (Gdx.input.isKeyPressed(Keys.RIGHT)|| Gdx.input.isKeyPressed(Keys.D))
        {            
            if (currentMap.moveRight())
            {
            	currentFrame = moveRight.getKeyFrame(stateTime, true);
            }
        }
        
        else if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
        {
        	if (currentMap.moveUp())
        	{
        		currentFrame = moveUp.getKeyFrame(stateTime, true); 
        	}
        }
        
        else if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
        { 
            if (currentMap.moveDown())
            {
            	currentFrame = moveDown.getKeyFrame(stateTime, true);
            }
        }
        
    }
    
    @Override
    public void draw(SpriteBatch batch) {
    	currentMap.draw(batch);
    	currentMap.update();
        batch.draw(currentFrame, currentMap.getCharDrawPosX(), currentMap.getCharDrawPosY());
    }
}
