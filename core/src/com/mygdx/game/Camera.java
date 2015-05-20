////////////////////////////////////////////////////////////////////////////////
//  Course:   CSC 151 Spring 2014
//  Section:  0001
// 
//  Project:  theGame-core
//  File:     Camera.java
//  
//  Name:     Bhavishya Shah
//  Email:    bhshah1@my.waketech.edu
////////////////////////////////////////////////////////////////////////////////
package com.mygdx.game;

/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Bhavishya Shah)
 *
 */
public class Camera
{
	
	public static Player focus;
	public boolean canMove;
	public Player ghost;
	
	
	public void haunt(Player set)
	{
		focus = set;
		ghost = focus;
		canMove = false;
	}
	
	public void unhaunt()
	{
		ghost = new Player();
		canMove = true;
	}
	
	public void rehaunt()
	{
		ghost = focus;
		canMove = false;
	}
	
	public void moveCamera(double x, double y, double speed)
	{
		if (canMove == true)
		{
			
		}
	}
}
