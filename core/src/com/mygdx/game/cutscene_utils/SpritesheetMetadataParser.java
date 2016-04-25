package com.mygdx.game.cutscene_utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * 
 * @author elimonent
 *
 * a class for reading in spritesheet metadata and creating
 * appropriate animations for a spritesheet based on that metadata
 */
public class SpritesheetMetadataParser {
	
	/**
	 *
	 * @param file - a spritesheet
	 * @return animations from that file in a dictionary
	 */
	public Map<String, Animation> getAnimations(FileHandle spritesheetFile) {
		Json json = new Json();
		SpritesheetMetadata metadata = json.fromJson(SpritesheetMetadata.class, getMetadataFileHandle(spritesheetFile));
		Texture spritesheetTexture = new Texture(spritesheetFile);
		
		int tileWidth = spritesheetTexture.getWidth() / metadata.width;
		int tileHeight = spritesheetTexture.getHeight() / metadata.height;

		TextureRegion[][] allFrames = TextureRegion.split(spritesheetTexture, tileWidth, tileHeight);
		Map<String, Animation> animationsMap = new HashMap<String, Animation>();
		
		for (String frameSetName: metadata.frameSets.keySet()) {
			int[] frameIndices = metadata.frameSets.get(frameSetName);
			TextureRegion[] frames = new TextureRegion[frameIndices.length];
			
			for (int i = 0; i < frameIndices.length; i++) {
				int oneDimIndex = frameIndices[i];
				int allFramesIndexX = oneDimIndex % metadata.width;
				int allFramesIndexY = frameIndices[i] / metadata.width;
				
				System.out.println(oneDimIndex);
				/*System.out.println(allFramesIndexX);
				System.out.println(allFramesIndexY);*/
				
				frames[i] = allFrames[allFramesIndexY][allFramesIndexX];
			}
			
			Animation animation = new Animation(0.025f, frames);
			animationsMap.put(frameSetName, animation);
		}
		
		return animationsMap;
	}

	static FileHandle getMetadataFileHandle(FileHandle file) {
		String name = file.file().getName().replace(".png", "_data.json");
		return Gdx.files.internal(name);
	}
}
