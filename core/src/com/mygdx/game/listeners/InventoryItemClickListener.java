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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.mygdx.game.Item;


public class InventoryItemClickListener extends InputListener {
	
	private Item item;
	private Skin skin;
	private List<Actor> toRemove;
	private List<Texture> toDisposeOf;
	private Stage stage;
	float moreInfoX;
	float moreInfoY;
	
	public InventoryItemClickListener(Item item, Skin skin, List<Actor> toRemove,
			List<Texture> toDisposeOf, Stage stage, float moreInfoX, float moreInfoY) {
		this.item = item;
		this.skin = skin;
		this.toRemove = toRemove;
		this.toDisposeOf = toDisposeOf;
		this.stage = stage;
		this.moreInfoX = moreInfoX;
		this.moreInfoY = moreInfoY;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		Table moreInfoTable = new Table(skin);
		moreInfoTable.align(Align.bottom);
		toRemove.add(moreInfoTable);
		
		//background for the more info popup
		Texture moreInfoBgTexture = new Texture(Gdx.files.internal("art/inventory/moreInfoBg.png"));
		toDisposeOf.add(moreInfoBgTexture);
		Image moreInfoBgImg = new Image(moreInfoBgTexture);
		moreInfoBgImg.setPosition(moreInfoX, moreInfoY);
		stage.addActor(moreInfoBgImg);
		toRemove.add(moreInfoBgImg);
		
		//put image of item in the mouse over table
		Texture itemTexture = new Texture(Gdx.files.internal(item.getInventoryImage()));
		Image itemImage = new Image(itemTexture);
		toRemove.add(itemImage);
		itemImage.setScaling(Scaling.fill);
		toDisposeOf.add(itemTexture);
		moreInfoTable.add(itemImage).width(30).height(30);
		moreInfoTable.row().padBottom(2);
		
		//put the item's name in the table
		Label itemNameLabel = new Label(item.getName(), skin.get("small", LabelStyle.class));
		toRemove.add(itemNameLabel);
		moreInfoTable.add(itemNameLabel).row();
		
		//add a button to drop the item from inventory
		TextButton dropButton = new TextButton("drop", skin);
		toRemove.add(dropButton);
		moreInfoTable.add(dropButton);
		
		moreInfoTable.setPosition(moreInfoBgImg.getX() + moreInfoBgImg.getWidth() / 2, moreInfoBgImg.getY() + 10);
		
		stage.addActor(moreInfoTable);
		return true;
	}
}
