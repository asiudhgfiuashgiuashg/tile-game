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
    //http://stackoverflow.com/questions/7069420/check-if-two-line-segments-are-colliding-only-check-if-they-are-intersecting-n
    public boolean intersects(LineSeg other) {
        Point thisP1 = this.p1;
        Point thisP2 = this.p2;

        Point otherP1 = other.getP1();
        Point otherP2 = other.getP2();

                   
        float q1 = (otherP2.getX() - otherP1.getX()) * (thisP1.getY() - otherP2.getY())
                    - (otherP2.getY() - otherP1.getY()) * (thisP1.getX() - otherP2.getX());///thisP1.minus(otherP1).cross(otherP2.minus(otherP1));

        float q2 = (otherP2.getX() - otherP1.getX()) * (thisP2.getY() - otherP2.getY())
                    - (otherP2.getY() - otherP1.getY()) * (thisP2.getX() - otherP2.getX()); 
//thisP2.minus(otherP1).cross(otherP2.minus(otherP1));

        float q3 = (thisP2.getX() - thisP1.getX()) * (otherP1.getY() - thisP2.getY())
                    - (thisP2.getY() - thisP1.getY()) * (otherP1.getX() - thisP2.getX()); 
//otherP2.minus(thisP1).cross(thisP2.minus(thisP1));

        float q4 = (thisP2.getX() - thisP1.getX()) * (otherP2.getY() - thisP2.getY())
                    - (thisP2.getY() - thisP1.getY()) * (otherP2.getX() - thisP2.getX()); 
//otherP1.minus(thisP1).cross(thisP2.minus(thisP1));
       return  ((q1 < 0 && q2 > 0) || (q1 > 0 && q2 < 0))
        && ((q3 < 0 && q4 > 0) || (q3 > 0 && q4 < 0));
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
