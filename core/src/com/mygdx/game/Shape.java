package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class Shape {
    //pos relative to origin of map
    Point pos;
    //list of lineSegments relative to pos
    private List<LineSeg> lineSegs;
    // highest/lowest x/y positions in the shape..used to determine if one shape is contained within another
    protected double furthestLeft, furthestRight, furthestUp, furthestDown;

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
    	
    	//check if lineSegs intersect
        List<LineSeg> otherLineSegs = other.getLineSegs();
        for (LineSeg otherSeg: otherLineSegs) {
            for (LineSeg thisSeg: this.lineSegs) {
                if (otherSeg.intersects(thisSeg)) {
                    return true;
                }
            }
        }

        //check if one shape is contained within the other in the case that none of their line segments touch
        //after this is checked, all conditions for collision will have been checked, so we can return the result of contains()
        return this.contains(other);
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
    
    // use this just in case shapes intersect, but none of their line segments collide (and thus arent picked up by the fancy line intersection crap)
    //  see instead if one shape wholly contains another
    private boolean contains(Shape other) {
    	         //this is in other
    	return (this.furthestDown > other.furthestDown && this.furthestUp < other.furthestUp && this.furthestRight < other.furthestRight && this.furthestLeft > other.furthestLeft)
    			//other is in this
    			|| (other.furthestDown > this.furthestDown && other.furthestUp < this.furthestUp && other.furthestRight < this.furthestRight && other.furthestLeft > this.furthestLeft);
    }
    
/*  
 *  Assume axises are oriented like this:
 *  
 *  ^
    |
    |
    |
    +-------->*/
    public void updateFurthests() {
    	for (LineSeg seg: this.lineSegs) {
    		Point p1 = seg.p1;
    		Point p2 = seg.p2;
    		
    		if (p1.getX() > furthestRight) {
    			furthestRight = p1.getX();
    		}
    		if (p2.getX() > furthestRight) {
    			furthestRight = p1.getX();
    		}
    		if (p1.getY() > furthestUp) {
    			furthestUp = p1.getY();
    		}
    		if (p2.getY() > furthestUp) {
    			furthestUp = p2.getY();
    		}
    		if (p1.getY() < furthestDown) {
    			furthestDown = p1.getY();
    		}
    		if (p2.getY() < furthestDown) {
    			furthestDown = p2.getY();
    		}
    		if (p1.getY() < furthestLeft) {
    			furthestLeft = p1.getY();
    		}
    		if (p2.getY() < furthestLeft) {
    			furthestLeft = p2.getY();
    		}
    	}
    }
    
    protected double getWidth() {
    	double smallestX = getSmallestXOfTwoPoints(lineSegs.get(0).p1, lineSegs.get(0).p2);
    	double largestX = getLargestXOfTwoPoints(lineSegs.get(0).p1, lineSegs.get(0).p2);
    	for (int i = 1; i < lineSegs.size(); i++) {
    		LineSeg lineSeg = lineSegs.get(i);
    		double smallestXOfLineSeg = getSmallestXOfTwoPoints(lineSeg.p1, lineSeg.p2);
    		double largestXOfLineSeg = getLargestXOfTwoPoints(lineSeg.p1, lineSeg.p2);
    		if (smallestX > smallestXOfLineSeg) {
    			smallestX = smallestXOfLineSeg;
    		}
    		if (largestX < largestXOfLineSeg) {
    			largestX = largestXOfLineSeg;
    		}
    	}
    	return largestX - smallestX;
    }
    
    private double getSmallestXOfTwoPoints(Point p0, Point p1) {
    	return p0.getX() < p1.getX() ? p0.getX() : p1.getX();
    }
    
    private double getLargestXOfTwoPoints(Point p0, Point p1) {
    	return p0.getX() > p1.getX() ? p0.getX() : p1.getX();
    }
}

