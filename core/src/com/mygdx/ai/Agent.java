package com.mygdx.ai;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Entity;
import com.mygdx.game.GameMap;
import com.mygdx.game.ObjectShape;
import com.mygdx.game.Point;
import com.mygdx.game.Shape;

public abstract class Agent extends Entity {
	protected Heuristic<PositionIndexedNode> heuristic; // heuristic for use in
														// A*

	public GraphPath<PositionIndexedNode> graphPath;
	protected enum State {
		MOVING_TOWARDS_FIRST_NODE, //move towards first node
		TRAVERSING_PATH, //traverse path determined by A*
		MOVING_TOWARDS_GOAL, //path is finished, move short distance to goal position
	}

	State movementState;
	Point goal;
	
	public Agent(ObjectShape shape, boolean passable) {
		super();
		// TODO Auto-generated constructor stub
		heuristic = new PositionalHeuristic();
		graphPath = null;
	}

	public void headTowards(double mapGoalPosX, double mapGoalPosY, DefaultIndexedGraphWithPublicNodes<PositionIndexedNode> graph) {
		goal = new Point(mapGoalPosX, mapGoalPosY);
		
		
		//identify nearest node to start position
		PositionIndexedNode nearestNodeToStart = getNearestNodeTo(pos, graph.getNodes());
		PositionIndexedNode nearestNodeToEnd = getNearestNodeTo(new Point(mapGoalPosX, mapGoalPosY), graph.getNodes());
		
		
		IndexedAStarPathFinder<PositionIndexedNode> pathFinder = new IndexedAStarPathFinder<PositionIndexedNode>(graph);
		
		GraphPath<PositionIndexedNode> graphPath = new DefaultGraphPath<PositionIndexedNode>();
		pathFinder.searchNodePath(nearestNodeToStart, nearestNodeToEnd, new PositionalHeuristic(), graphPath);
		this.graphPath = graphPath; //the graph path from the ~current position to the ~goal position
		movementState = State.MOVING_TOWARDS_FIRST_NODE;
	}
	
	private double dist(Point p0, Point p1) {
		return  Math.sqrt(
		            Math.pow(p0.getX() - p1.getX(), 2) +
		            Math.pow(p0.getY() - p1.getY(), 2));
	}
	
	private PositionIndexedNode getNearestNodeTo(Point point, Array<PositionIndexedNode> nodes) {
		PositionIndexedNode nearestNode = nodes.get(0);
		for (int i = 1; i < nodes.size; i++) {
			PositionIndexedNode currNode = nodes.get(i);
			if (dist(point, new Point(nearestNode.x, nearestNode.y)) > dist(point, new Point(currNode.x, currNode.y))) {
				nearestNode = currNode;
			}
		}
		
		return nearestNode;
	}
}
