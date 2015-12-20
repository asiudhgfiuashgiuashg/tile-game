package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


public class RemotePlayer extends Player
{
	private Animation currentAnimation;
	boolean idle;
	protected Label nameLabel;
	
	public RemotePlayer(Shape shape, boolean passable) {
		super(shape, passable);
		changeAppearance("Costume2.png");
		currentAnimation = moveLeft;
		super.setWidth(idleLeft.getRegionWidth());
	}
	
	@Override
    public void update(float stateTime) {
		if (!idle) {
			currentFrame = currentAnimation.getKeyFrame(stateTime, true);
		}
    }

	//map the String name of the animation sent by the server to actual Animation
	public void setAnimation(String animationName) {
		if ("idle".equals(animationName)) {
			idle = true;
		} else {
			idle = false;
			if ("walkLeft".equals(animationName)) {
				currentAnimation = moveLeft; //moveLeft is in Entity
			} else if ("walkRight".equals(animationName)) {
				currentAnimation = moveRight;
			} else if ("walkUp".equals(animationName)) {
				currentAnimation = moveUp;
			} else if ("walkDown".equals(animationName)) {
				currentAnimation = moveDown;
			}
		}
	}
}
