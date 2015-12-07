package com.mygdx.game;
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
    public void translate(double xDist, double yDist) {
    	x += xDist;
    	y += yDist;
    }

    //subtract other from this (this - other)
    public Point minus(Point other) {
        return new Point(this.getX() - other.getX(), this.getY() - other.getY());
    }

    //cross product of this point as a vector from origin crossed with other
    public double cross(Point other) {
        return this.getX() * other.getY() - this.getY() * other.getX();
    }
    
    @Override
    public String toString() {
    	return "(" + Double.toString(x) + ", " + Double.toString(y) + ")";
    }
    
    public Point plus(Point other) {
    	return this.minus(new Point(-other.getX(), -other.getY()));
    }
    //deep copy
    public Point deepCopy() {
    	return new Point(getX(), getY());
    }
}
