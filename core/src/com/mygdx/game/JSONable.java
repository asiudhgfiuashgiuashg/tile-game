/**
 * 
 */
package com.mygdx.game;

import org.json.simple.JSONObject;

/**
 * @author elimonent
 * a child of JSONable can be fully expressed (serialized) in JSON
 *  and thus can be read in from or written to JSON
 */
public interface JSONable {
	
	/**
	 * 
	 * @return a JSON representation of this object
	 */
	JSONObject toJSON();
	
	/**
	 * this method is not static because then it could not be part of the interface
	 * Because this is an instance method, subclasses will usually have some sort of dummy static instance field to call this method from
	 * @param json the json which contains all of the info needed to construct an object
	 * @return the object which was constructed from json
	 */
	JSONable fromJSON(JSONObject json);
}
