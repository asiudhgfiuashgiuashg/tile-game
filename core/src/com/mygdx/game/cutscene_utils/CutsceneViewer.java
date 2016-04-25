package com.mygdx.game.cutscene_utils;

import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CutsceneViewer extends ApplicationAdapter {
	SpriteBatch batch;
	Animation testAnimation;
	float stateTime;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		SpritesheetMetadataParser parser = new SpritesheetMetadataParser();
		Map<String, Animation> animations = parser.getAnimations(Gdx.files.internal("test_spritesheet.png"));
		testAnimation = animations.get("down_walk");
		testAnimation.setFrameDuration(.3f);
		
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = testAnimation.getKeyFrame(stateTime, true);  // #16
        batch.begin();
        batch.draw(currentFrame, 0, 0);
        batch.end();
	}
}
