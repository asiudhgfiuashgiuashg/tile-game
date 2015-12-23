package com.mygdx.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.Shape;

import java.util.Random;

/**
 * just wanders around right now
 * @author elimonent
 *
 */
public class TestAi extends Agent {
	private Animation currentAnimation;
	public GameMap map;
	private int indexOfCurrentPathDestination;
	
	public TestAi(Shape shape, boolean passable, GameMap map) {
		super(shape, passable);
		changeAppearance("art/orc.png");
		currentAnimation = moveLeft;
		currentFrame = idleLeft;
		setPos(new Point(100, 100));
		System.out.println("POSITION: " + pos);
		this.map = map;
	}


	@Override
	protected void update(float stateTime) {
		currentFrame = currentAnimation.getKeyFrame(stateTime, true);
		
		if (null == graphPath) {
			Random rand = new Random();
			headTowards(rand.nextInt(map.mapWidth), rand.nextInt(map.mapHeight), map);
			System.out.println("graph path size: " + graphPath.getCount());
			indexOfCurrentPathDestination = 0;
		}
		
		if (graphPath.getCount() > 0) { //(couldnt find a path, for example if the goal position unreachable)
			PositionIndexedNode currentNode = graphPath.get(indexOfCurrentPathDestination);
			double speed = 0.5;
			double newX = pos.getX();
			double newY = pos.getY();
			if (pos.getX() < currentNode.x) {
				newX += speed;
			} else {
				newX -= speed;
			}
			
			if (pos.getY() < currentNode.y) {
				newY += speed;
			} else {
				newY -= speed;
			}
			pos = new Point(newX, newY);
			
			if (closeTo(new Point(currentNode.x, currentNode.y), pos)) {
				System.out.println("close to node " + currentNode);
				indexOfCurrentPathDestination += 1;
				if (indexOfCurrentPathDestination < graphPath.getCount()) {
					currentNode = graphPath.get(indexOfCurrentPathDestination);
				} else {
					graphPath = null;
				}
			}
		} else {
			graphPath = null;
		}
	}
	
	private boolean closeTo(Point currentPos, Point desiredPos) {
		float dist = (float) Math.sqrt(
	            Math.pow(currentPos.getX() - desiredPos.getX(), 2) +
	            Math.pow(currentPos.getY() - desiredPos.getY(), 2));
		
		return dist < 5;
	}

	@Override
	public float getRight() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getLeft() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getTop() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getBottom() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setX(double newX) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setY(double newY) {
		// TODO Auto-generated method stub
		
	}
}
