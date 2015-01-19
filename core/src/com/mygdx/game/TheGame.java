package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input.Keys;

public class TheGame extends ApplicationAdapter 
{
	int FRAME_COLS = 13;
	int FRAME_ROWS = 21;

	Animation moveRight;
	Animation moveLeft;
	Animation moveUp;
	Animation moveDown;
	TextureRegion idleRight;
	TextureRegion idleLeft;
	TextureRegion idleUp;
	TextureRegion idleDown;
	SpriteBatch batch;
	Texture spriteSheet;
	TextureRegion[][] tmp;
	TextureRegion[] animationFrames;
	TextureRegion currentFrame;
	float stateTime;

	@Override
	public void create()
	{
		spriteSheet = new Texture(Gdx.files.internal("index.png"));
		tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAME_COLS, spriteSheet.getHeight() / FRAME_ROWS);
		
		moveLeft = animate(9, 9);
		idleLeft = tmp[9][0];
		moveRight = animate(11, 9);
		idleRight = tmp[11][0];
		moveUp = animate(8, 9);
		idleUp = tmp[8][0];
		moveDown = animate(10, 9);
		idleDown = tmp[10][0];
		batch = new SpriteBatch();
		currentFrame = idleUp;
		stateTime = 0f;
		

	}

	public Animation animate(int row, int length)
	{
		animationFrames = new TextureRegion[length];
		int index = 0;
		for (int j = 0; j < length; j++)
		{
			animationFrames[index++] = tmp[row][j];
		}
		Animation movement = new Animation(0.025f, animationFrames);;

		return movement;
	}

	private float charX = 200;
	private float charY = 100;

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		outerloop:
		if (Gdx.input.isKeyPressed(Keys.LEFT) && charX >= 0 || Gdx.input.isKeyPressed(Keys.A) && charX >= 0)
		{
			currentFrame = moveLeft.getKeyFrame(stateTime, true);
			charX -= 200 * Gdx.graphics.getDeltaTime();
			
			if (Gdx.input.isKeyPressed(Keys.LEFT) == false && Gdx.input.isKeyPressed(Keys.A) == false)
			{
				currentFrame = idleLeft;
				break outerloop;
			}
		}
		
		outerloop:
		if (Gdx.input.isKeyPressed(Keys.RIGHT) && charX <= 800 || Gdx.input.isKeyPressed(Keys.D) && charX <= 800)
		{
			if (Gdx.input.isKeyPressed(Keys.RIGHT) == false && Gdx.input.isKeyPressed(Keys.D) == false)
			{
				currentFrame = idleRight;
				break outerloop;
			}
			
			currentFrame = moveRight.getKeyFrame(stateTime, true);
			charX += 200 * Gdx.graphics.getDeltaTime();			
		}
		
		outerloop:
		if (Gdx.input.isKeyPressed(Keys.UP) && charY <= 480 || Gdx.input.isKeyPressed(Keys.W) && charY <= 480)
		{
			currentFrame = moveUp.getKeyFrame(stateTime, true);
			charY += 200 * Gdx.graphics.getDeltaTime();
			
			if (Gdx.input.isKeyPressed(Keys.UP) == false && Gdx.input.isKeyPressed(Keys.W) == false)
			{
				currentFrame = idleUp;
				break outerloop;
			}	
		}
		
		outerloop:
		if (Gdx.input.isKeyPressed(Keys.DOWN) && charY >= 0 || Gdx.input.isKeyPressed(Keys.S) && charY >= 0)
		{
			currentFrame = moveDown.getKeyFrame(stateTime, true);
			charY -= 200 * Gdx.graphics.getDeltaTime();

			if (Gdx.input.isKeyPressed(Keys.DOWN) == false && Gdx.input.isKeyPressed(Keys.S) == false)
			{
				currentFrame = idleDown;
				break outerloop;
			}	
		}
		batch.draw(currentFrame, charX - currentFrame.getRegionWidth()/2, charY - currentFrame.getRegionHeight()/2);
		
		batch.end();
	}
}