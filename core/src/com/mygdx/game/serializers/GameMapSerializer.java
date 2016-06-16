package com.mygdx.game.serializers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.GameMap;
import com.mygdx.game.ObjectCollector;
import com.mygdx.game.Tile;

/**
 * Can create a GameMap from json or create json representing a GameMap
 * @author elimonent
 *
 */
public class GameMapSerializer implements Serializer<GameMap> {

	/**
	 * convert gameMap to json
	 */
	@Override
	public void write(Json json, GameMap gameMap, Class knownType) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * convert json to an instance of GameMap
	 */
	@Override
	public GameMap read(Json json, JsonValue jsonData, Class type) {
		GameMap gameMap = new GameMap();
		
		int numRows = jsonData.getInt("height");
		int numCols = jsonData.getInt("width");
		gameMap.setNumRows(numRows);
		gameMap.setNumCols(numCols);
		
		gameMap.setMapTiles(new Tile[numRows][numCols]);
		
		/*
		 * get the 2D JSON array of tiles
		 */
		JsonValue tiles = jsonData.get("tiles");
		
		
		/*
		 * load tiles into gamemMap
		 */
		for (int r = 0; r < numRows; r++) {
			JsonValue row = tiles.get(r);
			for (int c = 0; c < numCols; c++ /* ha */) {
				String currTileArtURI = row.getString(c);
				String imageURI = currTileArtURI;
				String name = currTileArtURI.substring(0,
						currTileArtURI.indexOf('.')); // for now, the tile's
														// name can be its URI
														// without the extension
				boolean passable = true; // all tiles are passable for now

				Tile newTile = new Tile(c * GameMap.TILE_WIDTH, r * GameMap.TILE_HEIGHT,
						imageURI, name, passable);

				gameMap.getMapTiles()[numRows - 1 - r][c] = newTile;

			}
		}
		
		/*
		 * load objects into gameMap
		 */
		gameMap.setObjectList(new ObjectCollector(GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, numCols,
				numRows, gameMap.getMapTiles()));
		
		JsonValue objects = jsonData.get("objects");
		for (JsonValue objectMap : objects) {
			gameMap.getObjectList().addObject(objectMap);
		}
		
		gameMap.setMapWidth(GameMap.TILE_WIDTH * numCols);
		gameMap.setMapHeight(GameMap.TILE_HEIGHT * numRows);
		
		return gameMap;
	}

}
