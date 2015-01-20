package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TheGame extends ApplicationAdapter 
{
	Player player;
	SpriteBatch batch;
	float stateTime;

	@Override
	public void create()
	{	
		player = new Player();
		batch = new SpriteBatch();
		player.create();
		stateTime = 0f;
	}


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