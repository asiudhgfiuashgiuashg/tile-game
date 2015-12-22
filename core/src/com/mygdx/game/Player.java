////////////////////////////////////////////////////////////////////////////////
// 
//  Project:  theGame-core
//  File:     Player.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Entity
{

    GameMap currentMap;
    String username;
    
    int uid;
    
    protected ItemCollector inv;
    
    protected DirectionOfTravel direction;
    
    public Player(Shape shape, boolean passable)
    {
    	super(shape, passable);
    	username = "default username";
    	left = 15;
        right = 50;
        up = 55;
        down = 1;
    	
        pos = new Point(0, 0);

        inv = new ItemCollector();
        direction = DirectionOfTravel.IDLE;
    }
    
/*    public Player() {
		super()
	}*/


	
    
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
    	
    }
    

    public void drawAtPos(SpriteBatch batch, float drawPosX, float drawPosY) {
        batch.draw(currentFrame, drawPosX, drawPosY);
        //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY);
        //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY + getTop());
    }
    
    @Override
    public void draw(SpriteBatch batch) {
	    batch.draw(currentFrame, drawPosX, drawPosY);
	    //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY);
	    //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY + getTop());
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
    
    @Override
    public Point getPos() {
    	return pos;
    }
    @Override 
    public void setPos(Point newPos) {
    	pos = newPos;
    	shape.setPos(newPos);
    }
    @Override
    public void translate(Point dist) {
    	pos.plus(dist);
    	shape.translate(dist);
    }
    
    @Override
    public void setX(double newX) {
    	setPos(new Point(newX, pos.getY()));
    }
    
    @Override
    public void setY(double newY) {
    	setPos(new Point(pos.getX(), newY));
    }
    
    @Override
    public String toString() {
    	return username;
    }
}
