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
import com.mygdx.game.Point;
import com.mygdx.game.Shape;

public abstract class Agent extends Entity {
	protected Heuristic<PositionIndexedNode> heuristic; // heuristic for use in
														// A*

	protected GraphPath<PositionIndexedNode> graphPath;

	public Agent(Shape shape, boolean passable) {
		super(shape, passable);
		// TODO Auto-generated constructor stub
		heuristic = new PositionalHeuristic();

		graphPath = null;
	}

	public void headTowards(int mapGoalPosX, int mapGoalPosY, GameMap map) {
		Array<PositionIndexedNode> arrayCopy = GraphCreator.graphFromMap(map).getNodes();
		
		DefaultIndexedGraphWithPublicNodes<PositionIndexedNode> graph = new DefaultIndexedGraphWithPublicNodes<PositionIndexedNode>(arrayCopy);
		
		PositionIndexedNode startNode = new PositionIndexedNode((float) this
				.getPos().getX(), (float) this.getPos().getY(),
				graph.getNodeCount());

		graph.getNodes().add(startNode);
		
		
		PositionIndexedNode goalNode = new PositionIndexedNode(mapGoalPosX,
				mapGoalPosY, graph.getNodeCount());
		
		graph.getNodes().add(goalNode);

		GraphCreator.connectNode(startNode, graph.getNodes(), map);
		GraphCreator.connectNode(goalNode, graph.getNodes(), map);
		
		IndexedAStarPathFinder<PositionIndexedNode> pathFinder = new IndexedAStarPathFinder<PositionIndexedNode>(graph);
		GraphPath<PositionIndexedNode> graphPath = new DefaultGraphPath<PositionIndexedNode>();
		pathFinder.searchNodePath(startNode, goalNode, new PositionalHeuristic(), graphPath);
		
		this.graphPath = graphPath; //the graph path from the current position to the goal position
	}
}
