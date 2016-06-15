////////////////////////////////////////////////////////////////////////////////
// 
//  Project:  theGame-core
//  File:     Player.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game.player;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.DirectionOfTravel;
import com.mygdx.game.Entity;
import com.mygdx.game.GameMap;
import com.mygdx.game.ItemCollector;
import com.mygdx.game.Point;

public abstract class Player extends Entity {

    GameMap currentMap;
    public String username;
    
    public int uid;
    
    public ItemCollector inv;
    
    private DirectionOfTravel direction;
	public Label nameLabel;


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

	public DirectionOfTravel getDirection() {
		return direction;
	}

	public void setDirection(DirectionOfTravel direction) {
		this.direction = direction;
	}

	/**
	 * TODO
	 * make this per class (mage, ranger, shield)
	 * @return
	 */
	public float getMoveDist() {
		return 7;
	}
}
