package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.listeners.ItemListListener;

public class ExtendedStage extends Stage {
	
	protected GameMap currentMap;
	protected Skin skin;
	List<Item> itemList;
	
	@Override
	public boolean keyDown(int keycode) {
		if (!super.keyDown(keycode)) { //if not already handled by some listener on the stage, take action or do nothing based on the keycode
			if (currentMap != null && Input.Keys.G == keycode) {
				ItemCollector items = currentMap.getNearbyItemList();
				if (null == itemList) {
					//view items on ground nearby
					itemList = new List<Item>(skin);
					itemList.setItems(items.itemList);
					itemList.setPosition((float) currentMap.player.getPos().getX(), (float) currentMap.player.getPos().getY());
					itemList.addListener(new ItemListListener(itemList, currentMap));
					
					itemList.debug();

					this.addActor(itemList);
					this.setKeyboardFocus(itemList);
					
				} else {
					itemList.remove();
					itemList = null;
				}
				
				return true; //handled
			}
		} else {
			return true;
		}
		return false;//not handled
	}

	/**
	 * 
	 * @param uid
	 */
	public void updateItemList() {
		if (null != itemList) {
			Gdx.app.log(getClass().getSimpleName(), "updating item list: " + currentMap.getNearbyItemList().itemList);
			itemList.setItems(currentMap.getNearbyItemList().itemList);
		}
	}
}
