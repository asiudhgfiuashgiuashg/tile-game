package com.mygdx.game;

import java.io.IOException;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
		player = new Player();
		batch = new SpriteBatch();
		player.create();
		player.setFOV(player.sightX, player.sightY);
		
		Scanner sc = new Scanner(System.in);
        System.out.println("Which map would you like to test?");
        String mapName = "Test";//sc.nextLine();
        
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
		GuiManager.setGuiManager(mapGuiManager);
		itemListExists = false;
	}


	@Override
	public void render()
	{
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