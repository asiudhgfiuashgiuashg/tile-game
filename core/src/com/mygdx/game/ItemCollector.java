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

import java.io.FileNotFoundException;
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
	ArrayList<Item> itemList = new ArrayList<Item>();
	
	ItemCollector()
	{
		
	}
	
	ItemCollector(ArrayList<Item> items)
	{
		itemList = items;
	}
	
	public void addItem(String className, int id, int xPos, int yPos)
	{
		try
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
		catch(FileNotFoundException e)
		{
			System.out.println("Why would this even!");
		}
		
	}

	public boolean isEmpty()
	{
		if (itemList.isEmpty())
		{
			return true;
		}
		else
		{
		return false;
		}
	}
	
	public void moveItem(ItemCollector fromList)
	{
		
	}
	
	public ItemCollector createSubCollection(ArrayList<Integer> indexValues)
	{	
		ArrayList<Item> childItemList = new ArrayList<Item>();
		
		for (int x = 0; x < indexValues.size(); x++)
		{
			childItemList.add(itemList.get(indexValues.get(x)));
		}
		ItemCollector childItemCollector = new ItemCollector(childItemList);
		
		return childItemCollector;
	}
	public Item getItem(int index)
	{
		return itemList.get(index);
	}
	
	public String getItemName(int index)
	{
		return itemList.get(index).getName();
	}
	
	public int getXPos(int index)
	{
		return itemList.get(index).getXPos();
	}
	
	public int getYPos(int index)
	{
		return itemList.get(index).getYPos();
	}
	
	public String getFloorImage(int index)
	{
		return itemList.get(index).getFloorImage();
	}
	
	public String getInvImage(int index)
	{
		return itemList.get(index).getInventoryImage();
	}
	public int getWidth(int index)
	{
		return itemList.get(index).getWidth();
	}
	public int getHeight(int index)
	{
		return itemList.get(index).getHeight();
	}
	
	public int getItemListSize()
	{
		return itemList.size();
	}
	
}
