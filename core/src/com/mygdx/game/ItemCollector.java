////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     ItemCollector.java
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
public class ItemCollector
{
	ArrayList<Item> itemList;
	
	public void addItem(String className, int id, int xPos, int yPos)
	{
		if(className == "Weapon")
		{
			//not implemented
		}
		else
		{
			itemList.add( new Item(id, xPos, yPos));
		}
	}
	
	public int getItem
}
