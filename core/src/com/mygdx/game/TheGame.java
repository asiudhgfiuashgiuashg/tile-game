package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TheGame extends ApplicationAdapter 
{
/*	int FRAME_COLS = 13;
	int FRAME_ROWS = 21;

	Animation moveRight;
	Animation moveLeft;
	Animation moveUp;
	Animation moveDown;
	TextureRegion idleRight;
	TextureRegion idleLeft;
	TextureRegion idleUp;
	TextureRegion idleDown;
	Texture spriteSheet;
	TextureRegion[][] tmp;
	TextureRegion[] animationFrames;
	TextureRegion currentFrame; */
	Player player;
	SpriteBatch batch;
	float stateTime;

	@Override
	public void create()
	{		
		player.create();
		stateTime = 0f;
	}

/*	public Animation animate(int row, int length)
	{
		animationFrames = new TextureRegion[length];
		int index = 0;
		for (int j = 0; j < length; j++)
		{
			animationFrames[index++] = tmp[row][j];
		}
		Animation movement = new Animation(0.025f, animationFrames);

		return movement;
	} */



	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		
		player.update(stateTime);
		player.draw(batch);
		
		batch.end();
	}
}