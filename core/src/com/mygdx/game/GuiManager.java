////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     GuiManager.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;



/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
public class GuiManager {

    private GuiElement focused;
    List<GuiElement> listOfElements;
    List<GuiElement> visibleElements;
    private boolean listeningForInput;
    
    public GuiManager() {
        listOfElements = new ArrayList<GuiElement>();
        visibleElements = new ArrayList<GuiElement>();
    }
    
    public void addElement(GuiElement element) {
        listOfElements.add(element);
        visibleElements.add(element);
    }
    
    public void update() {
        for (GuiElement element: visibleElements) {
            element.update();
        }
        
        //listen for gui control input
        if (listeningForInput) {
        
        }
    }
    
    public void draw(SpriteBatch batch) {
        for (GuiElement element: visibleElements) {
            element.draw(batch);
        }
    }
    
    //listen for input
    public void listen() {
        listeningForInput = true;
    }
    //stop listening for input
    public void stopListening() {
        listeningForInput = false;
    }
}
