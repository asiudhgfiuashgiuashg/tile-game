////////////////////////////////////////////////////////////////////////////////
//  
//  Project:  theGame-core
//  File:     Entity.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity extends GameObject
{
	public Entity(Shape shape, boolean passable) {
		super(shape, passable);
	}
	
    int FRAME_COLS = 13;
    int FRAME_ROWS = 21;

    SpriteBatch batch;

    Animation moveRight;
    Animation moveLeft;
    Animation moveUp;
    Animation moveDown;
    Texture spriteSheet;
    TextureRegion[][] tmp;
    TextureRegion[] animationFrames;
    TextureRegion currentFrame;

    Point pos;
    float drawPosX;
    float drawPosY;
    //pixels relative to bottom left corner of current frame image
    float up;
    float down;
    float left;
    float right;
    
    //abstract void create(); why do we even have this instead of a constructor?
    abstract protected Animation animate(int row, int length);
    abstract void update(float stateTime);              // updates it in the case of movement or status changes

    abstract void draw(SpriteBatch batch);              // renders entity
    
    abstract public float getRight();
    abstract public float getLeft();
    abstract public float getTop();
    abstract public float getBottom();
    abstract public Point getPos();
    abstract public void setPos(Point point);
    public void translate(Point dist) {
    	shape.translate(dist);
    }
    abstract public void setX(float newX);
    abstract public void setY(float newY);
}