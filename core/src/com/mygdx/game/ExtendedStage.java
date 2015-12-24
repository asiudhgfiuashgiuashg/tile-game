package com.mygdx.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ExtendedStage extends Stage {
	
	protected GameMap currentMap;
	
	@Override
	public boolean keyDown(int keycode) {
		if (!super.keyDown(keycode)) { //if not handled
			if (currentMap != null && Input.Keys.G == keycode) {
				//view items on ground nearby
				ItemCollector items = currentMap.getNearbyItemList();
				System.out.println(items);
				return true; //handled
			}
		}
		return false;//not handled
	}
}
