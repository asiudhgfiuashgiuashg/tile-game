////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     GuiItemList.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

import java.util.ArrayList;


/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GuiItemList extends GuiElement
{
    ArrayList<Item> itemList;
    private int numberOfItems = 9;
    private BitmapFont font = new BitmapFont();
    
    void GuiElement()
    {
    	posX = 800;
        posY = 300;
    }
    
    public void setItemList(ArrayList<Item> list)
    {
        itemList = new ArrayList<Item>(list);
    }
    
    @Override
    public void update()
    {
    }
    
    
    public void displayItems(SpriteBatch batch)
    {
        String tempItemList = "";
        for (int x = 0; x < numberOfItems; x++)
        {
            tempItemList += itemList.get(x).toString() + "\n";
        }
        font.draw(batch, tempItemList, posY, posY);
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        displayItems(batch);
    }
}
