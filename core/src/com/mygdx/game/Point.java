package com.mygdx.game;
public class Point {
    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }
    public void translate(float xDist, float yDist) {
    	x += xDist;
    	y += yDist;
    }

    //subtract other from this (this - other)
    public Point minus(Point other) {
        return new Point(this.getX() - other.getX(), this.getY() - other.getY());
    }

    //cross product of this point as a vector from origin crossed with other
    public float cross(Point other) {
        return this.getX() * other.getY() - this.getY() * other.getX();
    }
    
    @Override
    public String toString() {
    	return "(" + Float.toString(x) + ", " + Float.toString(y) + ")";
    }
    
    public Point plus(Point other) {
    	return this.minus(new Point(-other.getX(), -other.getY()));
    }
    //deep copy
    public Point deepCopy() {
    	return new Point(getX(), getY());
    }
}
