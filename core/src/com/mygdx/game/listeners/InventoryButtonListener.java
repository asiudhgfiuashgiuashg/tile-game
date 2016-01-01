package com.mygdx.game.listeners;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.ExtendedStage;
import com.mygdx.game.Item;
import com.mygdx.game.LocalPlayer;
import com.mygdx.game.Player;
import com.mygdx.game.TheGame;

public class InventoryButtonListener extends InputListener {
	private ExtendedStage stage;
	protected Texture inventoryBgTexture;
	private LocalPlayer player;
	private Skin skin;
	private static List<Texture> toDisposeOf;
	private static List<Actor> toRemove;
	
	
	public InventoryButtonListener(ExtendedStage stage, LocalPlayer player, Skin skin) {
		this.stage = stage;
		this.player = player;
		toDisposeOf = new ArrayList<Texture>();
		toRemove = new ArrayList<Actor>();
		this.skin = skin;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		openInventoryOverlay();
		return true; //input handled
	}
	
	public void openInventoryOverlay() {
		
		//create background for inventory screen
		inventoryBgTexture = new Texture(Gdx.files.internal("art/inventory/inventoryBg.png"));
		Image inventoryBg = new Image(inventoryBgTexture);
		inventoryBg.setPosition(TheGame.SCREEN_WIDTH / 2 - inventoryBg.getWidth() / 2, (TheGame.SCREEN_HEIGHT - inventoryBg.getHeight()) / 2);
		stage.addActor(inventoryBg);
		toRemove.add(inventoryBg);
		
		//add pictures + text for all items in inventory
		Table invTable = new Table(skin);
		invTable.align(Align.topLeft);
		invTable.padLeft(20).padTop(10);
		invTable.setPosition(inventoryBg.getX(), inventoryBg.getY() + inventoryBg.getHeight());
		
		
		Array<Item> itemList = player.inv.itemList;
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
						toDisposeOf, stage, inventoryBg.getX() + inventoryBg.getWidth(), inventoryBg.getY() + inventoryBg.getHeight() / 1.5f));
				
				if (itemWidth * (j + 1) > inventoryBg.getWidth()) {
					j = 0;
					invTable.row().padTop(11);
				}
			}
		}
		
		stage.addActor(invTable);
		toRemove.add(invTable);
		
		//stick a close button on the inventory screen
		Texture closeButtonTexture = new Texture(Gdx.files.internal("art/inventory/closeButton.png"));
		Drawable closeButtonDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTexture));
		toDisposeOf.add(closeButtonTexture);
		
		Button closeButton = new Button(closeButtonDrawable);
		closeButton.setPosition(inventoryBg.getX() + inventoryBg.getWidth() - closeButton.getWidth() / 1.5f, inventoryBg.getY() + inventoryBg.getHeight() - closeButton.getHeight() / 1.5f);
		closeButton.addListener(new InventoryCloseButtonListener(toDisposeOf, toRemove));
		
		stage.addActor(closeButton);
		toRemove.add(closeButton);
		
	}
}
