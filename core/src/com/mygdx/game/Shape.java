package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class Shape {
    //pos relative to origin of map
    Point pos;
    //list of lineSegments relative to pos
    private List<LineSeg> lineSegs;

    //lineSegs should be specified relative to pos (pos is their origin, (0, 0))
    //pos should be specified relative to actual origin
    //lineSegs will then be converted to be relative to the actual origin, not pos
    public Shape(List<LineSeg> lineSegs, Point pos) {
        this.lineSegs = lineSegs;
        this.pos = pos;
        
        //conceptually, the Shape is created with its position at 0, 0 and then translated to its intended position
        updateLineSegs(new Point(0, 0));
    }
    public Shape() {
    	
    }
    //issue
    public void setPos(Point newPos) {
    	Point oldPos = pos;
        pos = newPos;
        
        updateLineSegs(oldPos);
    }
    
    private void updateLineSegs(Point oldPos) {
    	
    	double oldXDist = oldPos.getX(); //dist from map origin
    	double oldYDist = oldPos.getY();
        
    	double newXDist = pos.getX();
    	double newYDist = pos.getY();
        
    	double xDistDiff = newXDist - oldXDist;
    	double yDistDiff = newYDist - oldYDist;
        //move line segments of shape the same amount that the pos Point of the shape moved
        for (LineSeg seg: lineSegs) {
        	seg.translate(xDistDiff, yDistDiff); 	
        }
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
    
    public void translate(Point dist) { //where dist is a vector from origin representing the translation
    	setPos(pos.plus(dist));
    }
    
    public Shape deepCopy() {
    	List<LineSeg> copyOfLineSegs = new ArrayList<LineSeg>();
    	for (LineSeg seg: lineSegs) {
    		copyOfLineSegs.add(seg.deepCopy());
    	}
    	Shape copyOfShape = new Shape();
    	copyOfShape.setLineSegs(copyOfLineSegs);
    	copyOfShape.setPosNoUpdate(pos.deepCopy());
    	
    	return copyOfShape;
    }
    public Point getPos() {
    	return pos;
    }
    
    //used for deepCopy()
    public void setLineSegs(List<LineSeg> lineSegs) {
    	this.lineSegs = lineSegs;
    }
    //also used for deepCopy()
    public void setPosNoUpdate(Point newPos) {
    	this.pos = newPos;
    }
}

