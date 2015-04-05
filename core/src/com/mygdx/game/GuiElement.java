////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     GuiElement.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
public abstract class GuiElement {
    float posX;
    float posY;
    int width;
    int height;
    String imageLocation;
    protected boolean listeningForInput;
    
    public abstract void update();
    public abstract void draw(SpriteBatch batch);
    
    //listen for input
    public void listen() {
        listeningForInput = true;
    }
    //stop listening for input
    public void stopListening() {
        listeningForInput = false;
    }
}