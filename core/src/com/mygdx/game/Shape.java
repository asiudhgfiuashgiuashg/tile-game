package com.mygdx.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Shape {
	//pos relative to origin of map
	Point pos;
	//list of lineSegments relative to pos
	private List<LineSeg> lineSegs;
	// highest/lowest x/y positions in the shape..used to determine if one shape is contained within another
	protected double furthestLeft, furthestRight, furthestUp, furthestDown;
	/**
	 * y-value above which characters will be drawn behind the object. Like
	 * lineSegs, this value is initially relative to (0, 0) and should be set to
	 * the true world value
	 */
	private double cutoffY;

	//lineSegs should be specified relative to pos (pos is their origin, (0, 0))
	//pos should be specified relative to actual origin
	//lineSegs will then be converted to be relative to the actual origin, not pos
	public Shape(List<LineSeg> lineSegs, Point pos) {
		this.lineSegs = lineSegs;
		this.pos = pos;

		cutoffY = findCutoffY(lineSegs);

		//conceptually, the Shape is created with its position at 0, 0 and then translated to its intended position
		updateCoordinates(new Point(0, 0));
	}
	public Shape() {

	}

	/**
	 * Algorithm for finding cutoff point above which characters will be rendered behind the object
	 * 
	 * 1. extract points from lineSegs, ensuring that they are connected, ordered, non-intersecting
	 * 2. create list of points sorted by y coordinate
	 * 3. starting from the bottom, check for local maxima
	 * 4. if a point a small amount delta above the local maxima is inside of the shape, 
	 *  	that maxima is the peak of a nook. Check this using the following approach:
	 *  		i. create a line segment starting from the furthest left point that stretches 
	 *  			horizontally towards the point slightly beneath the local maxima in question
	 *  		ii. count number of intersections with shape's line segments
	 *  		iii. odd number of intersections --> point is inside the shape
	 * 5. if the local maxima is a nook, set the current cutoff value to it's y value
	 * 6. the highest nook will end up being the cutoff value. If there are no nooks, 
	 *  	the default value is the lowest point
	 *  
	 *  @param lineSegs line segments of shape
	 *  @return cutoff y-value
	 */
	private double findCutoffY(List<LineSeg> lineSegs) {
		// Extract points from lineSegs
		List<Point> points = derivePointsFromLineSegs(lineSegs);

		// Sort points by y value
		List<Point> sortedPoints = new ArrayList<Point>(points);
		sortedPoints.sort(new Comparator<Point>() {
			@Override
			public int compare(Point o1, Point o2) {
				return Double.compare(o1.getY(), o2.getY());
			}
		});

		// Arbitrarily small distance
		double delta = 0.001;
		
		// If no nook is found, the cutoff value is the lowest y-value of the points
		double cutoffY = sortedPoints.get(0).getY();
		for (Point p : sortedPoints) {
			int i = points.indexOf(p);
			Point pBefore, pAfter;
			if (i == 0) {
				pBefore = points.get(points.size() - 1);
				pAfter = points.get(i + 1);
			} else if (i == points.size() - 1) {
				pBefore = points.get(i - 1);
				pAfter = points.get(0);
			} else {
				pBefore = points.get(i - 1);
				pAfter = points.get(i + 1);
			}

			if (p.getY() >= pBefore.getY() && p.getY() >= pAfter.getY()) {
				// p is a local maxima
				Point pAbove = new Point(p.getX(), p.getY() + delta);
				// Check if pBelow is outside shape
				Point furthestLeft = new Point(this.furthestLeft - delta, pAbove.getY());
				LineSeg checkSeg = new LineSeg(furthestLeft, pAbove);
				int intersectionCount = 0;
				for (LineSeg other : lineSegs) {
					if (checkSeg.intersects(other)) {
						intersectionCount++;
					}
				}
				if (intersectionCount % 2 == 1) {
					// local maxima is a nook
					cutoffY = p.getY();
				}
			}
		}
		return cutoffY;
	}

	/**
	 * Helper method for findCutoffY.
	 * Extracts points from lineSegs and ensures they are ordered, connected, and non-intersecting
	 * @param lineSegs list of lineSegs
	 * @return list of points that constitute the shape
	 */
	private List<Point> derivePointsFromLineSegs(List<LineSeg> lineSegs) {
		// ASSERT: lineSegs must be connected, ordered, non-intersecting, and relative to origin (0, 0)

		// First check if line segs intersect each other
		if (this.intersects(this)) {
			throw new IllegalArgumentException("lineSegs intersect each other");
		}

		List<Point> points = new ArrayList<Point>();

		int size = lineSegs.size();

		// Check first and last lineSeg for connectivity, and add connection to list of points
		if (lineSegs.get(size - 1).getP2().equals(lineSegs.get(0).getP1())) {
			points.add(lineSegs.get(0).getP1());
		} else {
			throw new IllegalArgumentException("List of lineSegs is not connected or properly ordered");
		}

		// Check all the others
		for (int i = 1; i < size; i++) {
			if (lineSegs.get(i - 1).getP2().equals(lineSegs.get(i).getP1())) {
				points.add(lineSegs.get(i).getP1());
			} else {
				throw new IllegalArgumentException("List of lineSegs is not connected or properly ordered");
			}
		}
		return points;
	}

	//issue
	public void setPos(Point newPos) {
		Point oldPos = pos;
		pos = newPos;

		updateCoordinates(oldPos);
	}

	private void updateCoordinates(Point oldPos) {

		double oldXDist = oldPos.getX(); //dist from map origin
		double oldYDist = oldPos.getY();

		double newXDist = pos.getX();
		double newYDist = pos.getY();

		double xDistDiff = newXDist - oldXDist;
		double yDistDiff = newYDist - oldYDist;
		//move line segments of shape the same amount that the pos Point of the shape moved
		for (LineSeg seg : lineSegs) {
			seg.translate(xDistDiff, yDistDiff);
		}
		// shift the cutoff y-value
		cutoffY += yDistDiff;
	}

	public boolean intersects(Shape other) {

		//check if lineSegs intersect
		List<LineSeg> otherLineSegs = other.getLineSegs();
		for (LineSeg otherSeg : otherLineSegs) {
			for (LineSeg thisSeg : this.lineSegs) {
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
		for (LineSeg seg : getLineSegs()) {
			lineSegPrint += seg + "\n";
		}
		return "Shape:\n" + lineSegPrint;
	}

	public void translate(Point dist) { //where dist is a vector from origin representing the translation
		setPos(pos.plus(dist));
	}

	public Shape deepCopy() {
		List<LineSeg> copyOfLineSegs = new ArrayList<LineSeg>();
		for (LineSeg seg : lineSegs) {
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
		for (LineSeg seg : this.lineSegs) {
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

	protected void drawDebug(ShapeRenderer renderer, float offsetX, float offsetY) {
		renderer.setColor(Color.RED);
		for (LineSeg lineSeg : lineSegs) {
			renderer.line((float) lineSeg.p1.getX() + offsetX, (float) lineSeg.p1.getY() + offsetY, (float) lineSeg.p2.getX() + offsetX, (float) lineSeg.p2.getY() + offsetY);
		}
	}
}
