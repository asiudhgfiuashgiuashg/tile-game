package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class RemotePlayer extends Player
{
	private Animation currentAnimation;
	boolean idle;
	
	public RemotePlayer(Shape shape, boolean passable) {
		super(shape, passable);
		spriteSheet = new Texture(Gdx.files.internal("index2.png"));
		tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAME_COLS, spriteSheet.getHeight() / FRAME_ROWS);
		idle = true;
		moveLeft = animate(9, 9);
        idleLeft = tmp[9][0];
        moveRight = animate(11, 9);
        idleRight = tmp[11][0];
        moveUp = animate(8, 9);
        idleUp = tmp[8][0];
        moveDown = animate(10, 9);
        idleDown = tmp[10][0];
        currentFrame = idleUp;
		currentAnimation = moveLeft;
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
