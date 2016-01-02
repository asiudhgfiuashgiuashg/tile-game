package com.mygdx.game.listeners;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Disposable;

public class InventoryCloseButtonListener extends InputListener {
	List<Disposable> toDisposeOf;
	List<Actor> toRemove;
	
	public InventoryCloseButtonListener(List<Disposable> toDisposeOf, List<Actor> toRemove) {
		this.toDisposeOf = toDisposeOf;
		this.toRemove = toRemove;
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		closeInventoryOverlay();
		return true;
	}
	
	private void closeInventoryOverlay() {
		InventoryItemClickListener.removeAdditionalInfo();
		for (Actor actor: toRemove) {
			actor.remove(); //remove from parent (the stage)
		}
		for (Disposable disposable: toDisposeOf) {
			disposable.dispose(); //free up memory used by the inventory screen textures
		}
	}
}
