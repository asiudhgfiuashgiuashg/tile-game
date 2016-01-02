package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.listeners.InventoryCloseButtonListener;
import com.mygdx.game.listeners.InventoryItemClickListener;

public class InventoryGroup extends Group {
	private List<Disposable> toDisposeOf;
	private List<Actor> toRemove;
	
	public InventoryGroup(Skin skin, Array<Item> itemList) {
		toDisposeOf = new ArrayList<Disposable>();
		toRemove = new ArrayList<Actor>();
		
		
		//create background for inventory screen
		Texture inventoryBgTexture = new Texture(Gdx.files.internal("art/inventory/inventoryBg.png"));
		Image inventoryBg = new Image(inventoryBgTexture);
		inventoryBg.setPosition(TheGame.SCREEN_WIDTH / 2 - inventoryBg.getWidth() / 2, (TheGame.SCREEN_HEIGHT - inventoryBg.getHeight()) / 2);
		this.addActor(inventoryBg);
		toRemove.add(inventoryBg);
		
		//add pictures + text for all items in inventory
		Table invTable = new Table(skin);
		invTable.align(Align.topLeft);
		invTable.padLeft(20).padTop(10);
		invTable.setPosition(inventoryBg.getX(), inventoryBg.getY() + inventoryBg.getHeight());
		
		int itemWidth = 30;
		for (int i = 0, j = 0; i < itemList.size; i++, j++) {
			Item item = itemList.get(i);
			if (null != item) {
				Texture itemTexture = new Texture(Gdx.files.internal(item.getInventoryImage()));
				toDisposeOf.add(itemTexture);
				Image itemImage = new Image(itemTexture);
				itemImage.setScaling(Scaling.fill);
				invTable.add(itemImage).width(itemWidth).height(30).padRight(10);
				toRemove.add(itemImage);
				
				itemImage.addListener(new InventoryItemClickListener(item, skin, toRemove,
						toDisposeOf, this, inventoryBg.getX() + inventoryBg.getWidth(), inventoryBg.getY() + inventoryBg.getHeight() / 1.5f));
				
				if (itemWidth * (j + 1) > inventoryBg.getWidth()) {
					j = 0;
					invTable.row().padTop(11);
				}
			}
		}
		
		this.addActor(invTable);
		toRemove.add(invTable);
		
		//stick a close button on the inventory screen
		Texture closeButtonTexture = new Texture(Gdx.files.internal("art/inventory/closeButton.png"));
		Drawable closeButtonDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTexture));
		toDisposeOf.add(closeButtonTexture);
		
		Button closeButton = new Button(closeButtonDrawable);
		closeButton.setPosition(inventoryBg.getX() + inventoryBg.getWidth() - closeButton.getWidth() / 1.5f, inventoryBg.getY() + inventoryBg.getHeight() - closeButton.getHeight() / 1.5f);
		closeButton.addListener(new InventoryCloseButtonListener(toDisposeOf, toRemove));
		
		this.addActor(closeButton);
		toRemove.add(closeButton);
	}
}
