package com.mygdx.game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * handle input associated with controlling character
 * @author elimonent
 *
 */
public class GameInputProcessor implements InputProcessor {

	private LocalPlayer localPlayer;
	
	public GameInputProcessor(LocalPlayer localPlayer) {
		this.localPlayer = localPlayer;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		localPlayer.handleKeyDown(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		localPlayer.handleKeyUp(keycode);
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
