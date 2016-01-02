package com.mygdx.game.listeners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.GameMap;
import com.mygdx.game.InventoryGroup;
import com.mygdx.game.Item;
import com.mygdx.game.TheGame;

public class InventoryDropButtonListener extends InputListener {
	private Item item;
	private GameMap map;
	private InventoryGroup inventoryGroup;
	
	public InventoryDropButtonListener(Item item, InventoryGroup inventoryGroup) {
		this.item = item;
		this.map = TheGame.currentMap;
		this.inventoryGroup = inventoryGroup;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		item.pos = map.player.getPos().deepCopy();
		map.player.inv.itemList.removeValue(item, true);
		map.getItemList().addItem(item);
		inventoryGroup.removeItem(item);
		inventoryGroup.removeAdditionalInfo();
		return true;
	}
}
