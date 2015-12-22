package com.mygdx.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * A heuristic for the A* algorithm
 * @author elimonent
 *
 */
public class PositionalHeuristic implements Heuristic<PositionIndexedNode>{

	@Override
	public float estimate(PositionIndexedNode startNode, PositionIndexedNode endNode) {
		float dist = (float) Math.sqrt(Math.pow(startNode.x - endNode.x, 2) + Math.pow(startNode.y - endNode.y, 2));
		return dist;
	}

}
