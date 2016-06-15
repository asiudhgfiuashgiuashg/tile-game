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
import com.mygdx.game.TheGame;
import com.mygdx.game.player.Player;

public class InventoryButtonListener extends InputListener {
	private ExtendedStage stage;
	
	private Player localPlayer;
	private Skin skin;
	private static List<Texture> toDisposeOf;
	private static List<Actor> toRemove;
	
	
	public InventoryButtonListener(ExtendedStage stage, Player localPlayer, Skin skin) {
		this.stage = stage;
		this.localPlayer = localPlayer;
		toDisposeOf = new ArrayList<Texture>();
		toRemove = new ArrayList<Actor>();
		this.skin = skin;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		stage.openInventoryOverlay();
		return true; //input handled
	}
}
