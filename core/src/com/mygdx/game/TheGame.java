package com.mygdx.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class TheGame extends ApplicationAdapter 
{
	Player player;
	SpriteBatch batch;
	
	Map currentMap;
	GuiManager mapGuiManager; //holds all gui elements which are displayed when map is visible
	//GuiManager mainMenuGuiManager
	//etc
	boolean itemListExists;

	@Override
	public void create()
	{	
		System.out.println("SHAPE TESTING-------------------------------");
        /*LineSeg line1 = new LineSeg(new Point(0, 0), new Point(5, 5));
        LineSeg line2 = new LineSeg(new Point (6, 6), new Point(9, 9));
        LineSeg line3 = new LineSeg(new Point(2, 0), new Point(0, 2));
        LineSeg line4 = new LineSeg(new Point(10.4f, 1.02f), new Point(14.06f, 3.3f));
        LineSeg line5 = new LineSeg(new Point(10.15f, 3.3f), new Point(14.22f, 1.2f));
        LineSeg line6 = new LineSeg(new Point(0, 1), new Point(1, 0));
        LineSeg line7 = new LineSeg(new Point(1, 1), new Point(0, 0));
        LineSeg line8 = new LineSeg(new Point(6.05f, 9.4f), new Point(7.9f, 6.7f));
        LineSeg line9 = new LineSeg(new Point(5.28f, 6.47f), new Point(8.36f, 9.03f));
        System.out.println("line1, line2 intersection: " + line1.intersects(line2));
        System.out.println("line1, line3 intersection: " + line1.intersects(line3));
        System.out.println("line2, line3 intersection: " + line2.intersects(line3));
        System.out.println("line3, line4 intersection: " + line3.intersects(line4));
        System.out.println("line4, line5 intersection: " + line4.intersects(line5));
        System.out.println("line6, line7 intersection: " + line6.intersects(line7));
        System.out.println("line8, line9 intersection: " + line8.intersects(line9));
        System.out.println("line8, line7 intersection: " + line8.intersects(line7));
        System.out.println("line9, line8 intersection: " + line9.intersects(line8));*/

        Shape box1 = new Shape(Arrays.asList(new LineSeg(new Point(0, 0), new Point(0, 1)), 
        		new LineSeg(new Point(0, 1), new Point(1, 1)),
        		new LineSeg(new Point(1, 1), new Point(1, 0)),
        		new LineSeg(new Point(1, 0), new Point(0, 0))), new Point(0, 0));
        
        Shape box2 = new Shape(Arrays.asList(new LineSeg(new Point(0, 0), new Point(0, 1)), 
        		new LineSeg(new Point(0, 1), new Point(1, 1)),
        		new LineSeg(new Point(1, 1), new Point(1, 0)),
        		new LineSeg(new Point(1, 0), new Point(0, 0))), new Point(1f, 1f));
        
        System.out.println("box1: ");
        System.out.println(box1);
        System.out.println("box2: ");
        System.out.println(box2);
        System.out.println("box1, box2 collision: " + box1.intersects(box2));
		System.out.println("END SHAPE TESTING---------------------------");





		Shape playerShape = new Shape(Arrays.asList(
				new LineSeg(new Point(0, 0), new Point(0, 50)),
				new LineSeg(new Point(0, 50), new Point(50, 65)),
				new LineSeg(new Point(50, 65), new Point(65, 0)),
				new LineSeg(new Point(65, 0), new Point(0, 0))
				),
				new Point(0,0));
		
		player = new Player(playerShape, false);
		
		batch = new SpriteBatch();
		
		//player.create(); responsibilities for create() moved to constructor
		player.setFOV(player.sightX, player.sightY);
		
		Scanner sc = new Scanner(System.in);
        System.out.println("Which map would you like to test?");
        String mapName = "1Square";//sc.nextLine();
        
        sc.close();
        
		try
        {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            currentMap = new Map("../core/assets/" + mapName +".txt", "../core/assets/Tiles.txt", player);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to create map object");
        }
		
		//initialize various GuiManagers, giving them appropriate GuiElements
		mapGuiManager = new GuiManager();
		GuiManager.setCurrentManager(mapGuiManager);
		itemListExists = false;
	}


	@Override
	public void render()
	{
		//System.out.println(player.getShape());
		/*
		 * logic for switching between various GuiManagers could go here
		 * if (character.health <= 0) {
		 * 		GuiManager.setCurrentGuiManager(mainMenuGuiManager);
		 * } else if (...
		 */
		keyListening();
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		currentMap.draw(batch);
        currentMap.update(batch);
		GuiManager.currentManager.draw(batch);
		GuiManager.currentManager.update();
		
		batch.end();
	}
	
	public void keyListening() {
		GuiManager currentManager = GuiManager.getCurrentManager();
		if (currentManager.equals(mapGuiManager)) {
			if (Gdx.input.isKeyJustPressed(Keys.G)) {
				if (!itemListExists && !currentMap.getNearbyItemList().isEmpty()) {
					ItemCollector items = currentMap.getNearbyItemList();
					GuiItemList guiItemList = new GuiItemList(currentMap.player);
					guiItemList.setItemList(items);
					
					currentManager.addElement(guiItemList);
					currentManager.setFocused(guiItemList);
					itemListExists = true;
					player.setCanMove(false);
					currentManager.listen();
					
					guiItemList.watchedList = currentMap.itemsOnField;
				
					
					
				} else {
					
					currentManager.clearElements();
					itemListExists = false;
					player.setCanMove(true);
				}
				
			}
		}
/*		} else if (currentManager.equals(mainMenuGuiManager) {
			some other behavior
		} else if (currentManager.equals(someOtherGuiManager) {
			some other behavior
		}
		.
		.
		.
*/
		
		
	}
}
