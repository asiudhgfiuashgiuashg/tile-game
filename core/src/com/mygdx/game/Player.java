////////////////////////////////////////////////////////////////////////////////
// 
//  Project:  theGame-core
//  File:     Player.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity
{
    
    TextureRegion idleRight;
    TextureRegion idleLeft;
    TextureRegion idleUp;
    TextureRegion idleDown;
    private float moveSpeed;
    Map currentMap;
    int sightX = 400;
    int sightY = 240;
    
    private boolean canMove;
    
    public ItemCollector inv;
    
    @Override
    public void create()
    {
    	canMove = true;
    	moveSpeed = 200;
    	
        left = 15;
        right = 50;
        up = 50;
        down = 0;
        posX = 0;
        posY = 0;

        
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

        inv = new ItemCollector();

    }
    
    public void setCurrentMap(Map currentMap)
    {
    	this.currentMap = currentMap;
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
    	if (canMove()) {
	    	boolean left = (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) ? true : false;
	    	boolean right = (Gdx.input.isKeyPressed(Keys.RIGHT)|| Gdx.input.isKeyPressed(Keys.D)) ? true : false;
	    	boolean up = (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) ? true : false;
	    	boolean down = (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) ? true : false;
	    	
	    	try {
		        if (left && !right)
		        {
		            if (currentMap.moveLeft())
		            {
		                currentFrame = moveLeft.getKeyFrame(stateTime, true);
		            }
		        }
		        
		        else if (right && !left ) 
		        {            
		            if (currentMap.moveRight())
		            {
		                currentFrame = moveRight.getKeyFrame(stateTime, true);
		            }
		        }
		        
		        else if (up && !down)
		        {
		            if (currentMap.moveUp())
		            {
		                currentFrame = moveUp.getKeyFrame(stateTime, true); 
		            }
		        }
		        
		        else if (down && !up)
		        { 
		            if (currentMap.moveDown())
		            {
		                currentFrame = moveDown.getKeyFrame(stateTime, true);
		            }
		        }
	    	} catch(Exception e) {
	    		System.out.println(e.getMessage());
	    	}
    	}
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame, drawPosX, drawPosY);
        batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY);
        batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY + getTop());
    }
    
    
    @Override
    public float getRight() {
    	return right;
    }
    
    @Override
    public float getLeft() {
    	return left;
    }
    
    @Override
    public float getTop() {
    	return up;
    }
    
    @Override
    public float getBottom() {
    	return down;
    }
    public void setFOV(int x, int y)
    {
        sightX = x;
        sightY = y;
    }
    public void setCanMove(boolean canMove) {
    	this.canMove = canMove;
    }
    public boolean canMove() {
    	return canMove;
    }
    public float getMoveDist() {
    	return moveSpeed * Gdx.graphics.getDeltaTime();
    }
    
}
