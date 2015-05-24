package com.mygdx.game;

import java.util.List;

public class Shape {
    //pos relative to origin of map
    Point pos;
    //list of lineSegments relative to pos
    private List<LineSeg> lineSegs;

    //lineSegs should be specificed relative to pos (pos is their origin)
    //pos should be specified relative to actual origin
    //lineSegs will then be converted to be relative to the actual origin, not pos
    public Shape(List<LineSeg> lineSegs, Point pos) {
        this.lineSegs = lineSegs;
        this.pos = pos;

        //convert line segments to be relative to overall screen origin
        //(they were originally relative to pos)
        for (LineSeg lineSeg: this.lineSegs){
            //find out where pos lies relative to screen's origin
        	float xDist = pos.getX();
            float yDist = pos.getY();
            //use that information to express the line segment's position relative to screen's origin
            lineSeg.translate(xDist, yDist);
        }
    }
    //
    public void setPos(Point newPos) {
        this.pos = newPos;
    }

    public boolean intersects(Shape other) {
        List<LineSeg> otherLineSegs = other.getLineSegs();
        for (LineSeg otherSeg: otherLineSegs) {
            for (LineSeg thisSeg: this.lineSegs) {
                if (otherSeg.intersects(thisSeg)) {
                    return true;
                }
            }
        }

        return false; //none of the line segments of the two shapes intersected
    }

    public List<LineSeg> getLineSegs() {
        return this.lineSegs;
    }
    
    public String toString() {
    	String lineSegPrint = "";
    	for (LineSeg seg: getLineSegs()) {
    		lineSegPrint += seg + "\n";
    	}
    	return "Shape:\n" + lineSegPrint;
    }
}

