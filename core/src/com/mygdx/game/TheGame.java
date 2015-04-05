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
	GuiManager theGuiManager;
	
	boolean itemListExists = false;

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
		
		theGuiManager = new GuiManager();
	}


	@Override
	public void render()
	{
		keyListening();
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		currentMap.draw(batch);
        currentMap.update(batch);
		theGuiManager.draw(batch);
		
		batch.end();
	}
	
	public void keyListening() {
		if (Gdx.input.isKeyPressed(Keys.G)) {
			if (!itemListExists) {
				ItemCollector items = currentMap.getItemList();
				GuiItemList guiItemList = new GuiItemList();
				guiItemList.setItemList(items.itemList);
				
				theGuiManager.addElement(guiItemList);
				itemListExists = true;
			}
		}
	}
}