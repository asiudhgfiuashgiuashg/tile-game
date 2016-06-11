package com.mygdx.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class GameObject implements JSONable {
	/**
	 * a dummy instance from which fromJSON() may be called
	 * @see com.mygdx.game.JSONable#fromJSON()
	 * 
	 */
	private static final GameObject CREATOR = new GameObject();
	
	protected boolean passable;
	protected Shape shape;

	private String name;
	private int id;
	protected Point pos;
	protected int visLayer; //higher visLayer means the object is drawn later (on top of lower visLayers)


	String imageURI;

	private float width, height;

	public GameObject(Shape shape, boolean passable) {
		this.shape = shape;
		this.passable = passable;
	}
	
	public GameObject(boolean passable) {
		this.passable = passable;
	}
	
	public GameObject() {
		
	}

	

	public GameObject(boolean passable, int visLayer, Point pos, String imageURI, Shape shape) {
		this.passable = passable;
		this.visLayer = visLayer;
		this.pos = pos;
		this.imageURI = imageURI;
		this.shape = shape;
	}

	public int getId() {
		return id;
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public String getName() {
		return name;
	}
	public String getImage() {
		return imageURI;
	}

	public double getXPos() {
		return pos.getX();
	}
	public double getYPos() {
		return pos.getY();
	}


	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public void setHeight(float height) {
		this.height = height;
	}

	public void setImage(String URI) {
		this.imageURI = URI;
	}


	@Override
	public String toString() {
		return name.replace("_", " ");
	}

	public boolean isPassable() {
		return passable;
	}
	public Shape getShape() {
		return shape;
	}
	//returns true if the two objects take up some of the same space
	//like the middle of a venn diagram
	public boolean intersects(GameObject other) {
		return this.getShape().intersects(other.getShape());
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONable fromJSON(JSONObject json) {
		// TODO Auto-generated method stub
		return null;
	}
}