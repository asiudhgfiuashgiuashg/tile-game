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
import java.util.List;
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
	List<Item> itemList = new ArrayList<Item>();
	
	ItemCollector()
	{
		
	}
	
	ItemCollector(List<Item> items)
	{
		itemList = items;
	}
	
	public void addItem(String className, int id, Point pos)
	{
		try
		{
			if(className == "Weapon")
			{
				//not implemented
			}
			else
			{
				itemList.add( new Item(id, pos));
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Why would this even!");
		}
		
	}
	
	public void addItem(Item item)
	{
		itemList.add(item);
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
	
	public void moveItem(ItemCollector rootList, int index)
	{
		addItem(rootList.getItem(index));
		rootList.deleteItem(index);
	}
	
	public void deleteSet(ItemCollector deletedList)
	{
		for (int x = 0; x < deletedList.getListSize(); x++)
		{
			
			itemList.remove(deletedList.getItem(x));
			
			for (int y = 0; y < itemList.size(); y++)
			{
				System.out.println(itemList.get(y).getName());
			}
		}
	}
	
	public ItemCollector createSubCollection(List<Integer> indexValues)
	{	
		List<Item> childItemList = new ArrayList<Item>();
		
		for (int x = 0; x < indexValues.size(); x++)
		{
			childItemList.add(itemList.get(indexValues.get(x)));
		}
		ItemCollector childItemCollector = new ItemCollector(childItemList);
		
		return childItemCollector;
	}
	
	public void deleteItem(int index)
	{
		itemList.remove(index);
	}
	
	public void deleteItem(Item x)
	{
		itemList.remove(x);
	}
	
	public Item getItem(int index)
	{
		return itemList.get(index);
	}
	
	public String getItemName(int index)
	{
		return itemList.get(index).getName();
	}
	
	public double getXPos(int index)
	{
		return itemList.get(index).getXPos();
	}
	
	public double getYPos(int index)
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
	
	public int getListSize()
	{
		return itemList.size();
	}
	@Override
	public String toString() {
		String toReturn = "";
		for(Item item: itemList) {
			toReturn += item.toString() + '\n';
		}
		return toReturn;
	}
}
