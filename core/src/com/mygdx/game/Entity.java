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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.spritesheet_utils.SpritesheetMetadataParser;

public abstract class Entity extends GameObject {
	
    protected int FRAME_COLS = 13;
    protected int FRAME_ROWS = 21;
    
    protected static final float ANIMATION_DURATION = .125f;
    private static final float SPRITE_SCALE = 5f;
    SpriteBatch batch;

    protected Animation moveRight;
    protected Animation moveLeft;
    protected Animation moveUp;
    protected Animation moveDown;
    protected Texture spriteSheet;

    protected TextureRegion currentFrame;
    
    protected Animation idleRight;
    protected Animation idleLeft;
    protected Animation idleUp;
    protected Animation idleDown;
    
    protected String spriteResourceIdentifier;

    protected float drawPosX;
    protected float drawPosY;
    //pixels relative to bottom left corner of current frame image
    protected float up;
    protected float down;
    protected float left;
    protected float right;
    
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
    
    protected void setAppearance(FileHandle spritesheetFileHandle) {
        SpritesheetMetadataParser ssParser = new SpritesheetMetadataParser();
        Map<String, Animation> animations = ssParser.getAnimations(spritesheetFileHandle);
        for (String animationName: animations.keySet()) {
        	animations.get(animationName).setFrameDuration(ANIMATION_DURATION);
        }
        
        moveLeft = animations.get("left_walk");
        idleLeft = idleRight = idleUp = new Animation(1, moveLeft.getKeyFrame(0));
        moveRight = animations.get("right_walk");
        idleRight = idleUp = new Animation(1, moveRight.getKeyFrame(0));
        moveUp = animations.get("up_walk");
        idleUp = new Animation(1, moveUp.getKeyFrame(0));
        moveDown = animations.get("down_walk");
        idleDown = new Animation(1, moveDown.getKeyFrame(0));
        currentFrame = idleUp.getKeyFrame(0);
    }
    	

    
    public void drawAtPos(SpriteBatch batch, float drawPosX, float drawPosY) {
        batch.draw(currentFrame, drawPosX, drawPosY);
        //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY);
        //batch.draw(new Texture(Gdx.files.internal("red.png")), drawPosX + getLeft(), drawPosY + getTop());
    }
    
    public void draw(SpriteBatch batch) {
    	Sprite sprite = new Sprite(currentFrame);
    	sprite.setScale(SPRITE_SCALE);
    	sprite.setPosition(drawPosX, drawPosY);
    	sprite.draw(batch);

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