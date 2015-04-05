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
    Player player;
    
    public GuiItemList(Player player)
    {
    	this.player = player;
    	posX = player.drawPosX;
    	
    	if (player.drawPosY < 500)
    	{	
    		posY = player.drawPosY + player.up + 50;
    	}
    	else
    	{
    		posY = player.drawPosY - 100;
    	}
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
        for (int x = 0; x < numberOfItems && x < itemList.size(); x++)
        {
            tempItemList += itemList.get(x).toString() + "\n";
        }
        System.out.println(posX);
        font.drawMultiLine(batch, tempItemList, posX, posY);
        System.out.println(tempItemList);
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        displayItems(batch);
    }
}
