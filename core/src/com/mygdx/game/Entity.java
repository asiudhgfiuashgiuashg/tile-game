////////////////////////////////////////////////////////////////////////////////
//  
//  Project:  theGame-core
//  File:     Entity.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.spritesheet_utils.SpritesheetMetadataParser;

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
    
    protected Animation idleRight;
    protected Animation idleLeft;
    protected Animation idleUp;
    protected Animation idleDown;
    
    protected String spriteResourceIdentifier;

    protected float drawPosX;
    protected float drawPosY;
    //pixels relative to bottom left corner of current frame image
    float up;
    float down;
    float left;
    float right;
    
    //abstract void create(); why do we even have this instead of a constructor?
    protected abstract void update(float stateTime);              // updates it in the case of movement or status changes

    
    abstract public float getRight();
    abstract public float getLeft();
    abstract public float getTop();
    abstract public float getBottom();
    public void translate(Point dist) {
    	shape.translate(dist);
    }
    abstract public void setX(double newX);
    abstract public void setY(double newY);
    
    protected void changeAppearance(FileHandle spritesheetFileHandle)
    {
        SpritesheetMetadataParser ssParser = new SpritesheetMetadataParser();
        Map<String, Animation> animations = ssParser.getAnimations(spritesheetFileHandle);
        
        moveLeft = animations.get("move_left");
        idleLeft = animations.get("idle_left");
        moveRight = animations.get("move_right");
        idleRight = animations.get("idle_right");
        moveUp = animations.get("move_up");
        idleUp = animations.get("idle_up");
        moveDown = animations.get("move_down");
        idleDown = animations.get("idle_down");
        currentFrame = idleUp.getKeyFrame(0);
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
    
    public void drawAtPos(SpriteBatch batch, float drawPosX, float drawPosY) {
        batch.draw(currentFrame, drawPosX, drawPosY);
        //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY);
        //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY + getTop());
    }
    
    public void draw(SpriteBatch batch) {
	    batch.draw(currentFrame, drawPosX, drawPosY);
	    //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY);
	    //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY + getTop());
    }
    
    public Point getPos() {
    	return pos;
    }
    
    public void setPos(Point newPos) {
    	pos = newPos;
    	shape.setPos(newPos);
    }
}