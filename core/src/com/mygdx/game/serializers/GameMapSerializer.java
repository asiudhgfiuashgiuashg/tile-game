package com.mygdx.game.serializers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.GameMap;

/**
 * Can create a GameMap from json or create json representing a GameMap
 * @author elimonent
 *
 */
public class GameMapSerializer implements Serializer<GameMap> {

	@Override
	public void write(Json json, GameMap gameMap, Class knownType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameMap read(Json json, JsonValue jsonData, Class type) {
		// TODO Auto-generated method stub
		return null;
	}

}
