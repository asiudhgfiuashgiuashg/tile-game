package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

/*
* for use in shape class
*/

public class LineSeg {
    Point p1;
    Point p2;

    public LineSeg(Point p1, Point p2) {
    	// we deepcopy because otherwise LineSegs sharing points which are translated will be all fucked up (one point could be translated more
    	//  than once if looping through a list of lineSegs in a shape in which some lineSegs share Points.
        this.p1 = p1.deepCopy();
        this.p2 = p2.deepCopy();
    }
    
    //http://stackoverflow.com/questions/25830932/how-to-find-if-two-line-segments-intersect-or-not-in-java
    //http://stackoverflow.com/questions/7069420/check-if-two-line-segments-are-colliding-only-check-if-they-are-intersecting-n
    public boolean intersects(LineSeg other) {
        Point thisP1 = this.p1;
        Point thisP2 = this.p2;

        Point otherP1 = other.getP1();
        Point otherP2 = other.getP2();

                   
        double q1 = (otherP2.getX() - otherP1.getX()) * (thisP1.getY() - otherP2.getY())
                    - (otherP2.getY() - otherP1.getY()) * (thisP1.getX() - otherP2.getX());

        double q2 = (otherP2.getX() - otherP1.getX()) * (thisP2.getY() - otherP2.getY())
                    - (otherP2.getY() - otherP1.getY()) * (thisP2.getX() - otherP2.getX()); 


        double q3 = (thisP2.getX() - thisP1.getX()) * (otherP1.getY() - thisP2.getY())
                    - (thisP2.getY() - thisP1.getY()) * (otherP1.getX() - thisP2.getX()); 

        double q4 = (thisP2.getX() - thisP1.getX()) * (otherP2.getY() - thisP2.getY())
                    - (thisP2.getY() - thisP1.getY()) * (otherP2.getX() - thisP2.getX()); 

       return  ((q1 < 0 && q2 > 0) || (q1 > 0 && q2 < 0))
        && ((q3 < 0 && q4 > 0) || (q3 > 0 && q4 < 0));
    }
    
    public Point getP1() {
    	return p1;
    }
    public Point getP2() {
    	return p2;
    }
    
    public void translate(double xDist, double yDist) {
    	p1.translate(xDist, yDist);
    	p2.translate(xDist, yDist);
    }
    
    @Override
    public String toString() {
    	return "LineSeg: " + p1.toString() + "-----" + p2.toString();
    }
    public void setP1(Point newP1) {
    	p1 = newP1;
    }
    public void setP2(Point newP2) {
    	p2 = newP2;
    }
    //deep copy
    public LineSeg deepCopy() {
    	Point newP1 = p1.deepCopy();
    	Point newP2 = p2.deepCopy();
    	
    	return new LineSeg(newP1, newP2);
    }
}
