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
	private Communicator communicator;

	/**
	 * 
	 * @param shape the shape of the local player's hitbox
	 * @param passable whether the local player can be walked through by other players
	 * @param communicator what the local player uses to send updates to the server about its actions
	 */
	public LocalPlayer(Shape shape, boolean passable, Communicator communicator) {
		super(shape, passable);
		changeAppearance(Gdx.files.internal("character_art/ranger/ranger_spritesheet.png"));
		moveSpeed = 200;
		canMove = true;
		this.communicator = communicator;
	}

	public void setCurrentMap(GameMap currentMap) {
		directionStack = new Stack<DirectionOfTravel>();
		this.currentMap = currentMap;
	}
	
	
	
	
	@Override
	public void update(float stateTime) {
		if (canMove()) {
			if (!directionStack.isEmpty()) {
				direction = directionStack.peek();
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
					}
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
				
				/*
				 * send information about actions to server through the connector
				 */
				communicator.sendLocalPlayerPosition();
				communicator.sendLocalPlayerDirection();
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
		if (directionStack.isEmpty()) {
			direction = direction.IDLE;
		}
	}
	
	
}