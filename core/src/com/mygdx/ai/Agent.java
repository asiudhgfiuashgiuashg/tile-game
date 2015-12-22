package com.mygdx.ai;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Entity;
import com.mygdx.game.Point;
import com.mygdx.game.Shape;

public abstract class Agent extends Entity {
	protected Heuristic<PositionIndexedNode> heuristic; //heuristic for use in A*
	
	protected boolean hasPath;
	protected GraphPath<PositionIndexedNode> graphPath;

	public Agent(Shape shape, boolean passable) {
		super(shape, passable);
		// TODO Auto-generated constructor stub
		heuristic = new PositionalHeuristic();
		
		hasPath = false;
		graphPath = null;
	}
}
