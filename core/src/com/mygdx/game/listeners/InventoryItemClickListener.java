package com.mygdx.game.listeners;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.InventoryGroup;
import com.mygdx.game.Item;


public class InventoryItemClickListener extends InputListener {
	
	private Item item;
	private Skin skin;
	private List<Actor> toRemove;
	private List<Disposable> toDisposeOf;
	private InventoryGroup inventoryGroup;
	float moreInfoX;
	float moreInfoY;
	
	
	
	protected static boolean additionalInfoDisplayed;
	
	public InventoryItemClickListener(Item item, Skin skin, List<Actor> toRemove,
			List<Disposable> toDisposeOf, InventoryGroup inventoryGroup) {
		this.item = item;
		this.skin = skin;
		this.toRemove = toRemove;
		this.toDisposeOf = toDisposeOf;
		this.inventoryGroup = inventoryGroup;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		inventoryGroup.addAdditionalInfo(item);
		return true;
	}
}
