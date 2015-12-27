package com.mygdx.game.listeners;

import org.json.simple.JSONObject;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameMap;
import com.mygdx.game.Item;
import com.mygdx.game.TheGame;

public class ItemListListener extends InputListener {
	
	private List<Item> itemList;
	private GameMap currentMap;
	public ItemListListener(List<Item> itemList, GameMap map) {
		this.itemList = itemList;
		this.currentMap = map;
	}
	
	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		if (Input.Keys.S == keycode 
				|| Input.Keys.W == keycode
				|| Input.Keys.A == keycode 
				|| Input.Keys.D == keycode
				|| Input.Keys.ENTER == keycode) { //if trying to move selection down or up, also prevent character movement
			
			
			if (Input.Keys.S == keycode) {
				if (itemList.getSelectedIndex() < itemList.getItems().size - 1) {
					itemList.setSelectedIndex(itemList.getSelectedIndex() + 1);
				}
			} else if (Input.Keys.W == keycode) {
				if (itemList.getSelectedIndex() > 0) {
					itemList.setSelectedIndex(itemList.getSelectedIndex() - 1);
				}
			} else if (Input.Keys.ENTER == keycode) {
				if (itemList.getItems().size > 0) {
					Item itemToPickup = itemList.getSelected();
					//ask server if can pickup this item
					JSONObject itemPickupRequestMsg = new JSONObject();
					itemPickupRequestMsg.put("type", "itemPickupRequest");
					itemPickupRequestMsg.put("uid", itemToPickup.uid);
					TheGame.out.println(itemPickupRequestMsg);
				}
			}
			return true;
		}
		return false; //not handled
	}
}