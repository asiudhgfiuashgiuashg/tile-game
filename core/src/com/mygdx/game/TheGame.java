package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TheGame extends ApplicationAdapter {
	SpriteBatch batch;
	private BitmapFont font;
	private Sprite sprite;
	private static final int SHEET_WIDTH = 13;
	private static final int SHEET_HEIGHT = 21;
	private static final int NUM_WALK_FRAMES = 7;
	private static final int ROW_OF_LEFT_WALK = 9;
	private static final int ROW_OF_RIGHT_WALK = 11;
	private static final int ROW_OF_UP_WALK = 8;
	private static final int ROW_OF_DOWN_WALK = 10;
	float stateTime;
	
	Animation leftWalkAnimation;
	Animation rightWalkAnimation;
	Animation downWalkAnimation;
	Animation upWalkAnimation;
	Texture walkSheet;
	TextureRegion[] leftWalkFrames;
	TextureRegion[] rightWalkFrames;
	TextureRegion[] downWalkFrames;
	TextureRegion[] upWalkFrames;
	SpriteBatch spriteBatch;
	TextureRegion currentFrame;
	
	
	@Override
	public void create() {
		//animation stuff
		walkSheet = new Texture("index.png");
		TextureRegion[][] allFrames = TextureRegion.split(walkSheet, walkSheet.getWidth() / SHEET_WIDTH, walkSheet.getHeight() / SHEET_HEIGHT);
		leftWalkFrames = new TextureRegion[NUM_WALK_FRAMES];
		rightWalkFrames = new TextureRegion[NUM_WALK_FRAMES];
		downWalkFrames = new TextureRegion[NUM_WALK_FRAMES];
		upWalkFrames = new TextureRegion[NUM_WALK_FRAMES];
		for (int i = 0; i < 7; i++) {
			leftWalkFrames[i] = allFrames[ROW_OF_LEFT_WALK][i];
			rightWalkFrames[i] = allFrames[ROW_OF_RIGHT_WALK][i];
			upWalkFrames[i] = allFrames[ROW_OF_UP_WALK][i];
			downWalkFrames[i] = allFrames[ROW_OF_DOWN_WALK][i];
		}
		leftWalkAnimation = new Animation(0.03f, leftWalkFrames);
		downWalkAnimation = new Animation(0.03f, downWalkFrames);
		rightWalkAnimation = new Animation(0.03f, rightWalkFrames);
		upWalkAnimation = new Animation(0.03f, upWalkFrames);
		stateTime = 0;
		/////////////////////////
		batch = new SpriteBatch();
		Texture texture = new Texture(Gdx.files.internal("index.png"));
		sprite = new Sprite(texture, 20, 20, 50, 50);
		sprite.setPosition(10, 10);
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		
		currentFrame = allFrames[0][0];
	}
	
	private int charX = 0;
	private int charY = 0;

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			charX -= 200 * Gdx.graphics.getDeltaTime();
			currentFrame = leftWalkAnimation.getKeyFrame(stateTime, true);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			charX += 200 * Gdx.graphics.getDeltaTime();
			currentFrame = rightWalkAnimation.getKeyFrame(stateTime, true);
		}
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			charY += 200 * Gdx.graphics.getDeltaTime();
			currentFrame = upWalkAnimation.getKeyFrame(stateTime, true);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			charY -= 200 * Gdx.graphics.getDeltaTime();
			currentFrame = downWalkAnimation.getKeyFrame(stateTime, true);
		}
		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		batch.draw(currentFrame, charX, charY);
		batch.end();
	}
}