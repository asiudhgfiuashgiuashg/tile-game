package com.mygdx.game.spritesheet_utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * 
 * @author elimonent
 *
 * a class for reading in spritesheet metadata and creating
 * appropriate animations for a spritesheet based on that metadata
 * 
 * ~~~~~~~~~~~~~~~~metadata specification~~~~~~~~~~~~~~~~~~~~`
 *filename: image_name_data.json
 *
 *file format:
 * aseprite json array format
 *}
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class SpritesheetMetadataParser {
	
	/**
	 * given a spritesheet png its associated aseprite metadata, return a map of animation names -> animations
	 *  where the animations are composed of frames in the spritesheet
	 * @param file - a spritesheet
	 * @return animations from that file in a dictionary
	 */
	public Map<String, Animation> getAnimations(FileHandle spritesheetFile) {
		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		/*
		 * convert the spritesheet into a texture so we can split it up and make animations out of it
		 */
		Texture spritesheetTexture = new Texture(spritesheetFile);
		
		/*
		 * read the spritesheet metadata in from the file
		 */
		JsonValue jsonValue = (new JsonReader()).parse(getMetadataFileHandle(spritesheetFile));
		/*
		 * create a list of frames from the json metadata which can be indexed when putting frames toggether into animations
		 */
		JsonValue frames = jsonValue.get("frames");
		List<TextureRegion> frameTextureRegions = new ArrayList<TextureRegion>();
		for (JsonValue frame: frames) {
			JsonValue frameInfo = frame.get("frame");
			FrameMetadata frameMetadata = json.readValue(FrameMetadata.class, frameInfo);
			TextureRegion frameTextureRegion = new TextureRegion(spritesheetTexture, frameMetadata.x, frameMetadata.y, frameMetadata.w, frameMetadata.h);
			frameTextureRegions.add(frameTextureRegion);
		}
		
		/*
		 * a map from the animation's name to the animation itself
		 * users of animations (each of the classes, AI, ...) will reference the animation they need with its name as the key
		 */
		Map<String, Animation> animationsMap = new HashMap<String, Animation>();
		
		/*
		 * read each frame tag and create an animation for it, then add it to the map under the appropriate name
		 */
		JsonValue frameTags = jsonValue.get("meta").get("frameTags");
		for (JsonValue frameTagJson: frameTags) {
			FrameTagMetadata frameTagMetadata = json.readValue(FrameTagMetadata.class, frameTagJson);
			/*
			 * form the animation corresponding to this frame tag
			 */
			Array<TextureRegion> frameTagFrames = new Array<TextureRegion>();
			for (int i = frameTagMetadata.from; i <= frameTagMetadata.to; i++) {
				frameTagFrames.add(frameTextureRegions.get(i));
			}
			Animation animation = new Animation(.025f, frameTagFrames);
			/*
			 * put the animaiton in the map under its name
			 */
			animationsMap.put(frameTagMetadata.name, animation);
		}
		
		return animationsMap;
	}

	static FileHandle getMetadataFileHandle(FileHandle file) {
		String name = file.path().replace(".png", ".json");
		System.out.println("fetching metadata with filename: " + name);
		return Gdx.files.internal(name);
	}
}
