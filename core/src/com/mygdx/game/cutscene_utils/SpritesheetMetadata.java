package com.mygdx.game.cutscene_utils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 
 * @author elimonent
 *
 * represents metadata for a spritesheet
 */
public class SpritesheetMetadata {
	protected int width;
	protected int height;
	
	protected HashMap<String, int[]> frameSets;
	
	@Override
	public String toString() {
		String toReturn = "";
		toReturn += "width: " + width + "\n";
		toReturn += "height: " + height + "\n";
		toReturn += "frameSets: " + frameSets;
		
		return toReturn;
	}
}
