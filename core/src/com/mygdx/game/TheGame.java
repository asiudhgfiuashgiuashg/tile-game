package com.mygdx.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

public class TheGame extends ApplicationAdapter 
{
	Player player;
	SpriteBatch batch;
	
	Map currentMap;
	GuiManager mapGuiManager; //holds all gui elements which are displayed when map is visible
	GuiManager mainMenuGuiManager;
	//GuiManager mainMenuGuiManager
	//etc
	boolean itemListExists;

	@Override
	public void create()
	{	
		
		Shape shape = new Shape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 55)),
				new LineSeg(new Point(15, 55), new Point(50, 55)),
				new LineSeg(new Point(50, 55), new Point(50, 0)),
				new LineSeg(new Point(50, 0), new Point(15, 0))
				),
				new Point(0,0));
		player = new Player(shape, false);
		
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
		mainMenuGuiManager = new GuiManager();
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
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		if(GuiManager.currentManager.equals(mapGuiManager))
		{
			currentMap.draw(batch);
			currentMap.update(batch);
		}
		GuiManager.currentManager.draw(batch);
		GuiManager.currentManager.update();
		
		batch.end();
	}
	
	public void keyListening() {
		
		if (GuiManager.currentManager.equals(mapGuiManager)) 
		{
			if (Gdx.input.isKeyJustPressed(Keys.G)) {
				if (!itemListExists && !currentMap.getNearbyItemList().isEmpty()) {
					ItemCollector items = currentMap.getNearbyItemList();
					GuiItemList guiItemList = new GuiItemList(currentMap.player, 0);
					guiItemList.setItemList(items);
					
					GuiManager.currentManager.addElement(guiItemList);
					GuiManager.currentManager.setFocused(guiItemList);
					itemListExists = true;
					player.setCanMove(false);
					GuiManager.currentManager.listen();
					
					guiItemList.watchedList = currentMap.itemsOnField;
				
					
					
				} else {
					
					GuiManager.currentManager.clearElements();
					itemListExists = false;
					player.setCanMove(true);
				}
				
			}
			if (Gdx.input.isKeyJustPressed(Keys.C))
			{	
				GuiManager.currentManager = mainMenuGuiManager;
			}
		}
		else if (GuiManager.currentManager.equals(mainMenuGuiManager))
			{
				if (Gdx.input.isKeyJustPressed(Keys.C))
				{	
					GuiManager.currentManager = mapGuiManager;
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
