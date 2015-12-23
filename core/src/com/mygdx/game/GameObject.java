package com.mygdx.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameObject {
	protected boolean passable;
	protected Shape shape;

	private String name;
	private int id;
	protected Point pos;

	public static final String classFileLocation = "../core/assets/Object.txt";

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

	public GameObject(int id, Point pos) throws FileNotFoundException {
		this.id = id;
		this.pos = pos;

		Scanner objectFileScanner = new Scanner(new File(classFileLocation));

		//find corresponding line in object file
		String currentLine = null;
		String[] currentAttributes = null;
		boolean found = false;
		while (!found) {
			currentLine = objectFileScanner.nextLine();
			currentAttributes = currentLine.split(", ");
			int idFromObjectFile = Integer.parseInt(currentAttributes[1]);
			if (idFromObjectFile == this.id) { //id matches object we want
				found = true;
			}
		}

		objectFileScanner.close();
		//now currentAttributes should correspond to the attributes of the object we want from the object file
		setName(currentAttributes[0]);
		setId(Integer.parseInt(currentAttributes[1]));
		setImage(currentAttributes[2]);
		setWidth(Float.parseFloat(currentAttributes[3]));
		setHeight(Float.parseFloat(currentAttributes[4]));
		List < LineSeg > lines = new ArrayList < LineSeg > ();
		List < Point > points = new ArrayList < Point > ();
		for (int x = 3; x < currentAttributes.length; x += 2) {
			points.add(new Point(Float.parseFloat(currentAttributes[x]), Float.parseFloat(currentAttributes[x + 1])));
		}
		for (int i = 0; i < points.size() - 1; i++) {
			lines.add(new LineSeg(points.get(i), points.get(i + 1)));
		}
		lines.add(new LineSeg(points.get(points.size() - 1), points.get(0)));
		System.out.println("pos: " + pos);
		shape = new Shape(lines, pos);

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
}