package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class LocalPlayer extends Player {
	boolean isMovingLeft;
	boolean isMovingRight;
	boolean isMovingUp;
	boolean isMovingDown;
	Direction direction;

	private float moveSpeed;
	int sightX = 400;
	int sightY = 240;

	private boolean canMove;

	public LocalPlayer(Shape shape, boolean passable) {
		super(shape, passable);
		changeAppearance("index.png");
		moveSpeed = 200;
		canMove = true;

		// TODO Auto-generated constructor stub
	}

	public void setCurrentMap(GameMap currentMap) {
		this.currentMap = currentMap;
	}@Override
	public void update(float stateTime) {
		if (canMove()) {
			isMovingLeft = (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) ? true : false;
			isMovingRight = (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) ? true : false;
			isMovingUp = (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) ? true : false;
			isMovingDown = (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) ? true : false;

			try {
				//Each direction has preset limits for the character pos to help prevent outofbounds errors and to smoothen movement along the edges. Once collision is perfected, these should'nt be necessary
				if (isMovingLeft && !isMovingRight) {
					if (pos.getX() > 0 - this.left && currentMap.moveLeft()) {
						currentFrame = moveLeft.getKeyFrame(stateTime, true);
						direction = Direction.LEFT;
					}
				} else if (isMovingRight && !isMovingLeft) {
					if (pos.getX() < currentMap.mapWidth - this.right && currentMap.moveRight()) {
						currentFrame = moveRight.getKeyFrame(stateTime, true);
						direction = Direction.RIGHT;
					}
				} else if (isMovingUp && !isMovingDown) {
					if (pos.getY() < currentMap.mapHeight - this.up && currentMap.moveUp()) {
						currentFrame = moveUp.getKeyFrame(stateTime, true);
						direction = Direction.UP;

					}
				} else if (isMovingDown && !isMovingUp) {
					if (pos.getY() > this.down && currentMap.moveDown()) {
						currentFrame = moveDown.getKeyFrame(stateTime, true);
						direction = Direction.DOWN;
					}
				} else {
					direction = null;
					// no direction, just stopped
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	public void setFOV(int x, int y) {
		sightX = x;
		sightY = y;
	}
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}
	public boolean canMove() {
		return canMove;
	}
	public float getMoveDist() {
		return moveSpeed * Gdx.graphics.getDeltaTime();
	}
}