package com.mygdx.game;
public class Point {
    private double x;
    private double y;
    private final double EPSILON = .000001;

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
    //http://www.ideyatech.com/effective-java-equals-and-hashcode/
    /**
     * equality is approximate (see EPSILON)
     */
    @Override
    public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Point)) {
			return false;
		}
		final Point theOther = (Point) other;
		return Math.abs(this.getX() - theOther.getX()) < EPSILON && Math.abs(this.getY() - theOther.getY()) < EPSILON;
    }
    @Override
    public int hashCode() {
    	return (int) (this.getY() * 13 + this.getX());
    }
}
