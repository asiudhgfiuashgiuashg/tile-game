////////////////////////////////////////////////////////////////////////////////
//  
//  Project:  theGame-core
//  File:     Entity.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Entity extends GameObject
{
	public Entity(Shape shape, boolean passable) {
		super(shape, passable);
	}
	
    protected int FRAME_COLS = 13;
    protected int FRAME_ROWS = 21;

    SpriteBatch batch;

    protected Animation moveRight;
    protected Animation moveLeft;
    protected Animation moveUp;
    protected Animation moveDown;
    protected Texture spriteSheet;
    protected TextureRegion[][] tmp;
    protected TextureRegion[] animationFrames;
    protected TextureRegion currentFrame;
    
    protected TextureRegion idleRight;
    protected TextureRegion idleLeft;
    protected TextureRegion idleUp;
    protected TextureRegion idleDown;
    
    protected String spriteResourceIdentifier;

    float drawPosX;
    float drawPosY;
    //pixels relative to bottom left corner of current frame image
    float up;
    float down;
    float left;
    float right;
    
    //abstract void create(); why do we even have this instead of a constructor?
    abstract protected Animation animate(int row, int length);
    protected abstract void update(float stateTime);              // updates it in the case of movement or status changes

    protected abstract void draw(SpriteBatch batch);              // renders entity
    
    abstract public float getRight();
    abstract public float getLeft();
    abstract public float getTop();
    abstract public float getBottom();
    abstract public Point getPos();
    abstract public void setPos(Point point);
    public void translate(Point dist) {
    	shape.translate(dist);
    }
    abstract public void setX(double newX);
    abstract public void setY(double newY);
    
    protected void changeAppearance(String sprite)
    {
    	this.spriteResourceIdentifier = sprite;
    	spriteSheet = new Texture(Gdx.files.internal(this.spriteResourceIdentifier));
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
    }
}