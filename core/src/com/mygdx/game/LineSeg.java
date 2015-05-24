package com.mygdx.game;

/*
* for use in shape class
*/

public class LineSeg {
    Point p1;
    Point p2;

    public LineSeg(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
    
    //http://stackoverflow.com/questions/25830932/how-to-find-if-two-line-segments-intersect-or-not-in-java
    public boolean intersects(LineSeg other) {
        Point thisP1 = this.p1;
        Point thisP2 = this.p2;

        Point otherP1 = other.getP1();
        Point otherP2 = other.getP2();

       return thisP1.minus(otherP1).cross(otherP2.minus(otherP1)) < 0
        && thisP2.minus(otherP1).cross(otherP2.minus(otherP1)) > 0
        && otherP2.minus(thisP1).cross(thisP2.minus(thisP1)) < 0
        && otherP1.minus(thisP1).cross(thisP2.minus(thisP1)) > 0;
    }
    
    public Point getP1() {
    	return p1;
    }
    public Point getP2() {
    	return p2;
    }
    
    public void translate(float xDist, float yDist) {
    	p1.translate(xDist, yDist);
    	p2.translate(xDist, yDist);
    }
}
