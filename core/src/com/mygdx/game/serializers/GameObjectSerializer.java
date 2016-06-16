package com.mygdx.game.serializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.GameObject;
import com.mygdx.game.LineSeg;
import com.mygdx.game.ObjectShape;
import com.mygdx.game.Point;

public class GameObjectSerializer implements Serializer<GameObject> {

	@Override
	public void write(Json json, GameObject object, Class knownType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameObject read(Json json, JsonValue objectMap, Class type) {
		/*
		 * get a json map of the object's basic properties
		 */
		JsonValue baseProperties = objectMap.get("baseProperties");
		boolean passable =  baseProperties.getBoolean("collision");
		int visLayer = baseProperties.getInt("visLayer");
		double xPos = baseProperties.getDouble("x");
		double yPos = baseProperties.getDouble("y");
		String fileName = baseProperties.getString("fileName");
		Point pos = new Point(xPos, yPos);
		
		/*
		 * TODO after the shape-maker is combined with the map tool, have object's shapes be part of their json
		 *  and make a  ShapeSerializer to read the shape
		 */
		List<LineSeg> shapeLineSegs = new ArrayList<LineSeg>();
		ObjectShape shape = new ObjectShape(Arrays.asList(
				new LineSeg(new Point(15, 0), new Point(15, 55)),
				new LineSeg(new Point(15, 55), new Point(50, 55)),
				new LineSeg(new Point(50, 55), new Point(50, 0)),
				new LineSeg(new Point(50, 0), new Point(15, 0))
				),
				new Point(0,0));
		
		GameObject newObject = new GameObject(passable, visLayer, pos, fileName, shape);
		
		return newObject;
	}

}
