package com.mygdx.game;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.player.Player;

/**
 * handle input associated with controlling character
 * @author elimonent
 *
 */
public class GameInputProcessor implements InputProcessor {

	/**
	 * used to figure out which way to move based on keypresses.
	 * Every time a movement key is pressed put its direction on the stack
	 * When a movement key is released, take its direction off the stack
	 * When choosing a direction, look at the top of the stack.
	 * This is null movement (tf2 kek)
	 */
	protected static final Stack<DirectionOfTravel> directionStack = new Stack<DirectionOfTravel>();
	
	/**
	 * the player who will be controlled by wsad
	 */
	private Player localPlayer;
	

	public GameInputProcessor(Player localPlayer) {
		this.localPlayer = localPlayer;
	}
	
	/**
	 * move the local player according to input
	 * @param stateTime the time since the last frame
	 * @throws Exception 
	 */
	protected void moveLocalPlayer(float stateTime) throws Exception {
		if (DirectionOfTravel.LEFT == localPlayer.getDirection()) {
			TheGame.currentMap.moveLeft();
		} else if (DirectionOfTravel.RIGHT == localPlayer.getDirection()) {
			TheGame.currentMap.moveRight();
		} else if (DirectionOfTravel.UP == localPlayer.getDirection()) {
			TheGame.currentMap.moveUp();
		} else if (DirectionOfTravel.DOWN == localPlayer.getDirection()) {
			TheGame.currentMap.moveDown();
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		/*
		 * first handle basic inputs that will do the same thing for every class 
		 */
		if ((Input.Keys.LEFT == keycode) || (Input.Keys.A == keycode)) {
			directionStack.add(DirectionOfTravel.LEFT);
		} else if ((Input.Keys.RIGHT == keycode) || (Input.Keys.D == keycode)) {
			directionStack.add(DirectionOfTravel.RIGHT);
		} else if ((Input.Keys.UP == keycode) || (Input.Keys.W == keycode)) {
			directionStack.add(DirectionOfTravel.UP);
		} else if ((Input.Keys.DOWN == keycode) || (Input.Keys.S == keycode)) {
			directionStack.add(DirectionOfTravel.DOWN);
		}
		localPlayer.setDirection(directionStack.peek());
		/*
		 * next give everything unhandled to the handler for the localplayer's specific class (mage, ranger, shield)
		 */

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		/*
		 * first handle basic inputs that will do the same thing for every class 
		 */
		
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
			localPlayer.setDirection(DirectionOfTravel.IDLE);
		} else {
			localPlayer.setDirection(directionStack.peek());
		}
		/*
		 * next give everything unhandled to the handler for the localplayer's specific class (mage, ranger, shield)
		 */

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
