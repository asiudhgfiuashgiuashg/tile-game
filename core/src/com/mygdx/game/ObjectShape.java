package com.mygdx.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * a shape with an associated cutoff y which is derived upon shape creation
 * designed to be used as a hitbox for an object
 * required to be a closed shape (https://www.splashmath.com/math-vocabulary/geometry/closed-shape)
 * no line segments may intersect
 * @author elimonent
 *
 */
public class ObjectShape extends Shape {
	/**
	 * y-value above which characters will be drawn behind the object. Like
	 * lineSegs, this value is initially relative to (0, 0) and should be set to
	 * the true world value
	 */
	private double cutoffY;
	
	public ObjectShape(List<LineSeg> lineSegs, Point pos) {
		this.lineSegs = lineSegs;
		this.pos = pos;

		cutoffY = findCutoffY(lineSegs);

		//conceptually, the Shape is created with its position at 0, 0 and then translated to its intended position
		updateCoordinates(new Point(0, 0));
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
	 * 5. if the local maxima is a nook, set the current cutoff value to its y value
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
	
	/**
	 * when coordinate are updated, also update the cutOffY
	 */
	@Override
	public double updateCoordinates(Point oldPos) {
		double yDistDiff = super.updateCoordinates(oldPos);
		
		// shift the cutoff y-value
		cutoffY += yDistDiff;
		
		return yDistDiff;
	}
}
