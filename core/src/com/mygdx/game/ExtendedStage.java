package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ExtendedStage extends Stage {
	
	protected GameMap currentMap;
	protected Skin skin;
	private List<Item> itemList;
	
	@Override
	public boolean keyDown(int keycode) {
		if (!super.keyDown(keycode)) { //if not already handled by some listener on the stage, take action or do nothing based on the keycode
			if (currentMap != null && Input.Keys.G == keycode) {
				if (null == itemList) {
					//view items on ground nearby
					ItemCollector items = currentMap.getNearbyItemList();
					itemList = new List<Item>(skin);
					itemList.setItems(items.itemList);
					itemList.setPosition((float) currentMap.player.getPos().getX(), (float) currentMap.player.getPos().getY());
					itemList.addListener(new InputListener() {
						@Override
						public boolean keyDown(InputEvent event, int keycode) {
							if (Input.Keys.S == keycode) {
								if (itemList.getSelectedIndex() < itemList.getItems().size - 1) {
									itemList.setSelectedIndex(itemList.getSelectedIndex() + 1);
								}
								return true; //handled
							} else if (Input.Keys.W == keycode) {
								if (itemList.getSelectedIndex() > 0) {
									itemList.setSelectedIndex(itemList.getSelectedIndex() - 1);
								}
								return true; //handled
							}
							return false; //not handled
						}
					});
					
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
}
