////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     ObjectCollector.java
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
public class ObjectCollector
{
	List<Object> objectList;
	
	ObjectCollector()
	{
		objectList = new ArrayList<Object>();
	}
	
	ObjectCollector(List<Object> objects)
	{
		objectList = objects;
	}
	
	public void addObject(String className, int id, Point pos)
	{
		System.out.println("apart");
		try
		{
			if(className == "Weapon")
			{
				//not implemented
			}
			else
			{
				objectList.add( new Object(id, pos));
			}
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Why would this even!");
		}
		
	}
	
	public void deleteItem(int index)
	{
		objectList.remove(index);
	}
	
	public void deleteItem(Item x)
	{
		objectList.remove(x);
	}
	
	public Object getObject(int index)
	{
		return objectList.get(index);
	}
	
	public String getObjectName(int index)
	{
		return objectList.get(index).getName();
	}
	
	public float getXPos(int index)
	{
		return objectList.get(index).getXPos();
	}
	
	public float getYPos(int index)
	{
		return objectList.get(index).getYPos();
	}
	
	public String getImage(int index)
	{
		return objectList.get(index).getImage();
	}
	public int getListSize()
	{
		return objectList.size();
	}
}
