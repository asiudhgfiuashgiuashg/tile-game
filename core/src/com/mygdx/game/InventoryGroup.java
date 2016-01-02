package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.listeners.InventoryCloseButtonListener;
import com.mygdx.game.listeners.InventoryDropButtonListener;
import com.mygdx.game.listeners.InventoryItemClickListener;

public class InventoryGroup extends Group {
	private List<Disposable> toDisposeOf;
	private List<Actor> toRemove;
	private Skin skin;
	
	private Table moreInfoTable;
	private Image moreInfoBgImg;
	private Label itemNameLabel;
	private Image itemImage;
	private TextButton dropButton;
	
	private boolean additionalInfoDisplayed;
	private float moreInfoX;
	private float moreInfoY;
	
	private Array<Item> itemList;
	private Table invTable;
	private Image inventoryBg;
	
	/**
	 * construct an overlay which displays inventory items
	 * @param skin
	 * @param itemList
	 */
	public InventoryGroup(Skin skin, Array<Item> itemList) {
		this.skin = skin;
		toDisposeOf = new ArrayList<Disposable>();
		toRemove = new ArrayList<Actor>();
		this.itemList = itemList;
		
		additionalInfoDisplayed = false;
		
		
		//create background for inventory screen
		Texture inventoryBgTexture = new Texture(Gdx.files.internal("art/inventory/inventoryBg.png"));
		inventoryBg = new Image(inventoryBgTexture);
		inventoryBg.setPosition(TheGame.SCREEN_WIDTH / 2 - inventoryBg.getWidth() / 2, (TheGame.SCREEN_HEIGHT - inventoryBg.getHeight()) / 2);
		this.addActor(inventoryBg);
		toRemove.add(inventoryBg);
		
		//position info for where to place the additional info popup
		moreInfoX = inventoryBg.getX() + inventoryBg.getWidth();
		moreInfoY = inventoryBg.getY() + inventoryBg.getHeight() / 1.5f;
		
		//add pictures + text for all items in inventory
		invTable = new Table(skin);
		invTable.align(Align.topLeft);
		invTable.padLeft(20).padTop(10);
		invTable.setPosition(inventoryBg.getX(), inventoryBg.getY() + inventoryBg.getHeight());
		
		populateInvTable();
		
		this.addActor(invTable);
		toRemove.add(invTable);
		
		//stick a close button on the inventory screen
		Texture closeButtonTexture = new Texture(Gdx.files.internal("art/inventory/closeButton.png"));
		Drawable closeButtonDrawable = new TextureRegionDrawable(new TextureRegion(closeButtonTexture));
		toDisposeOf.add(closeButtonTexture);
		
		Button closeButton = new Button(closeButtonDrawable);
		closeButton.setPosition(inventoryBg.getX() + inventoryBg.getWidth() - closeButton.getWidth() / 1.5f, inventoryBg.getY() + inventoryBg.getHeight() - closeButton.getHeight() / 1.5f);
		closeButton.addListener(new InventoryCloseButtonListener(toDisposeOf, toRemove, this));
		
		this.addActor(closeButton);
		toRemove.add(closeButton);
	}

	public void addAdditionalInfo(Item item) {
		//avoid stacking info popups on top of each other
		if (additionalInfoDisplayed) {
			removeAdditionalInfo();
		}
		
		moreInfoTable = new Table(skin);
		moreInfoTable.align(Align.bottom);
		//toRemove.add(moreInfoTable);
		
		//background for the more info popup
		Texture moreInfoBgTexture = new Texture(Gdx.files.internal("art/inventory/moreInfoBg.png"));
		toDisposeOf.add(moreInfoBgTexture);
		moreInfoBgImg = new Image(moreInfoBgTexture);
		moreInfoBgImg.setPosition(moreInfoX, moreInfoY);
		this.addActor(moreInfoBgImg);
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
		dropButton.addListener(new InventoryDropButtonListener(item, this));
		moreInfoTable.add(dropButton);
		
		moreInfoTable.setPosition(moreInfoBgImg.getX() + moreInfoBgImg.getWidth() / 2, moreInfoBgImg.getY() + 10);
		
		this.addActor(moreInfoTable);
		
		
		additionalInfoDisplayed = true;
	}
	
	public void removeAdditionalInfo() {
		if (additionalInfoDisplayed) {
			moreInfoTable.remove();
			moreInfoBgImg.remove();
			itemNameLabel.remove();
			dropButton.remove();
			//consider also disposing of textures in this function instead of waiting for inventory screen to be closed to dispose of them
			additionalInfoDisplayed = false;
		}
	}

	public void removeItem(Item item) {
		itemList.removeValue(item, true);
		populateInvTable();
	}
	
	public void populateInvTable() {
		invTable.clear();
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
						toDisposeOf, this));
				
				if (itemWidth * (j + 1) > inventoryBg.getWidth()) {
					j = 0;
					invTable.row().padTop(11);
				}
			}
		}
	}
}