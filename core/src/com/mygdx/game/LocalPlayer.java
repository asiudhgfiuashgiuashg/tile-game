package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Stack;

public class LocalPlayer extends Player {
	private float moveSpeed;
	int sightX = 400;
	int sightY = 240;

	private boolean canMove;
	protected Stack<DirectionOfTravel> directionStack; //used to figure out which way to move based on keypresses

	public LocalPlayer(Shape shape, boolean passable) {
		super(shape, passable);
		sprite = "Costume1.png";
		changeAppearance();
		moveSpeed = 200;
		canMove = true;

		// TODO Auto-generated constructor stub
	}

	public void setCurrentMap(GameMap currentMap) {
		directionStack = new Stack<DirectionOfTravel>();
		this.currentMap = currentMap;
	}
	
	
	
	
	@Override
	public void update(float stateTime) {
		if (canMove()) {
			try {
				//Each direction has preset limits for the character pos to help prevent outofbounds errors and to smoothen movement along the edges. Once collision is perfected, these should'nt be necessary
				if (DirectionOfTravel.LEFT == directionStack.peek()) {
					if (pos.getX() > 0 - this.left && currentMap.moveLeft()) {
						currentFrame = moveLeft.getKeyFrame(stateTime, true);
					}
				} else if (DirectionOfTravel.RIGHT == directionStack.peek()) {
					if (pos.getX() < currentMap.mapWidth - this.right && currentMap.moveRight()) {
						currentFrame = moveRight.getKeyFrame(stateTime, true);
					}
				} else if (DirectionOfTravel.UP == directionStack.peek()) {
					if (pos.getY() < currentMap.mapHeight - this.up && currentMap.moveUp()) {
						currentFrame = moveUp.getKeyFrame(stateTime, true);

					}
				} else if (DirectionOfTravel.DOWN == directionStack.peek()) {
					if (pos.getY() > this.down && currentMap.moveDown()) {
						currentFrame = moveDown.getKeyFrame(stateTime, true);
					}
				} else {
					direction = DirectionOfTravel.IDLE;
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
	
	protected void handleKeyDown(int keycode) {
		if ((Input.Keys.LEFT == keycode) || (Input.Keys.A == keycode)) {
			directionStack.add(DirectionOfTravel.LEFT);
		} else if ((Input.Keys.RIGHT == keycode) || (Input.Keys.D == keycode)) {
			directionStack.add(DirectionOfTravel.RIGHT);
		} else if ((Input.Keys.UP == keycode) || (Input.Keys.W == keycode)) {
			directionStack.add(DirectionOfTravel.UP);
		} else if ((Input.Keys.DOWN == keycode) || (Input.Keys.S == keycode)) {
			directionStack.add(DirectionOfTravel.DOWN);
		}
	}

	public void handleKeyUp(int keycode) {
		if ((Input.Keys.LEFT == keycode) || (Input.Keys.A == keycode)) {
			directionStack.remove(DirectionOfTravel.LEFT);
		} else if ((Input.Keys.RIGHT == keycode) || (Input.Keys.D == keycode)) {
			directionStack.remove(DirectionOfTravel.RIGHT);
		} else if ((Input.Keys.UP == keycode) || (Input.Keys.W == keycode)) {
			directionStack.remove(DirectionOfTravel.UP);
		} else if ((Input.Keys.DOWN == keycode) || (Input.Keys.S == keycode)) {
			directionStack.remove(DirectionOfTravel.DOWN);
		}
	}
}