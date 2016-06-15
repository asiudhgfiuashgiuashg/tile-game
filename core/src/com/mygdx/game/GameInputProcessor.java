package com.mygdx.game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.game.player.Player;

/**
 * handle input associated with controlling character
 * @author elimonent
 *
 */
public class GameInputProcessor implements InputProcessor {

	private Player localPlayer;
	
	public GameInputProcessor(Player localPlayer) {
		this.localPlayer = localPlayer;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		/*
		 * first handle basic inputs that will do the same thing for every class 
		 */
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
