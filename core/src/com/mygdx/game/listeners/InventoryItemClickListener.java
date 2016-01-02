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
	
	protected static Table moreInfoTable;
	protected static Image moreInfoBgImg;
	protected static Label itemNameLabel;
	protected static Image itemImage;
	protected static TextButton dropButton;
	
	protected static boolean additionalInfoDisplayed;
	
	public InventoryItemClickListener(Item item, Skin skin, List<Actor> toRemove,
			List<Disposable> toDisposeOf, InventoryGroup inventoryGroup, float moreInfoX, float moreInfoY) {
		this.item = item;
		this.skin = skin;
		this.toRemove = toRemove;
		this.toDisposeOf = toDisposeOf;
		this.inventoryGroup = inventoryGroup;
		this.moreInfoX = moreInfoX;
		this.moreInfoY = moreInfoY;
		
		if (additionalInfoDisplayed) {
			removeAdditionalInfo();
		}
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		
		moreInfoTable = new Table(skin);
		moreInfoTable.align(Align.bottom);
		//toRemove.add(moreInfoTable);
		
		//background for the more info popup
		Texture moreInfoBgTexture = new Texture(Gdx.files.internal("art/inventory/moreInfoBg.png"));
		toDisposeOf.add(moreInfoBgTexture);
		moreInfoBgImg = new Image(moreInfoBgTexture);
		moreInfoBgImg.setPosition(moreInfoX, moreInfoY);
		inventoryGroup.addActor(moreInfoBgImg);
		//toRemove.add(moreInfoBgImg);
		
		//put image of item in the mouse over table
		Texture itemTexture = new Texture(Gdx.files.internal(item.getInventoryImage()));
		itemImage = new Image(itemTexture);
		//toRemove.add(itemImage);
		itemImage.setScaling(Scaling.fill);
		toDisposeOf.add(itemTexture);
		moreInfoTable.add(itemImage).width(30).height(30);
		moreInfoTable.row().padBottom(2);
		
		//put the item's name in the table
		itemNameLabel = new Label(item.getName(), skin.get("small", LabelStyle.class));
		//toRemove.add(itemNameLabel);
		moreInfoTable.add(itemNameLabel).row();
		
		//add a button to drop the item from inventory
		dropButton = new TextButton("drop", skin);
		//toRemove.add(dropButton);
		dropButton.addListener(new InventoryDropButtonListener(item));
		moreInfoTable.add(dropButton);
		
		moreInfoTable.setPosition(moreInfoBgImg.getX() + moreInfoBgImg.getWidth() / 2, moreInfoBgImg.getY() + 10);
		
		inventoryGroup.addActor(moreInfoTable);
		
		
		additionalInfoDisplayed = true;
		return true;
	}
	
	public static void removeAdditionalInfo() {
		if (additionalInfoDisplayed) {
			moreInfoTable.remove();
			moreInfoBgImg.remove();
			itemNameLabel.remove();
			dropButton.remove();
			//consider also disposing of textures in this function instead of waiting for inventory screen to be closed to dispose of them
			additionalInfoDisplayed = false;
		}
	}
}
