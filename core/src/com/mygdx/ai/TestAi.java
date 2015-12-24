package com.mygdx.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.DirectionOfTravel;
import com.mygdx.game.GameMap;
import com.mygdx.game.Player;
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
	private int indexOfCurrentPathDestination;;
	double speed;
	boolean followingPlayer;
	Player player;
	Point playerOldPos;
	
	public TestAi(Shape shape, boolean passable, GameMap map) {
		super(shape, passable);
		changeAppearance("art/orc.png");
		currentAnimation = moveLeft;
		currentFrame = idleLeft;
		setPos(new Point(100, 100));
		System.out.println("POSITION: " + pos);
		this.map = map;
		
		indexOfCurrentPathDestination = 0;
		speed = 1.5;
		followingPlayer = false;
		playerOldPos = new Point(-100, -100);
	}
	
	public void setFollowPlayer(Player player, boolean follow) {
		this.player = player;
		this.followingPlayer = follow;
	}

	@Override
	protected void update(float stateTime) {
		currentFrame = currentAnimation.getKeyFrame(stateTime, true);
		
		if (followingPlayer) {
			if (!closeTo(player.getPos(), playerOldPos)) {		
				headTowards(player.getPos().getX(), player.getPos().getY(), map.nodeGraph);
				indexOfCurrentPathDestination = 0;
				playerOldPos = player.getPos();
			}
		} else {
			if (null == movementState) {
					Random rand = new Random();
					headTowards(rand.nextInt(map.mapWidth), rand.nextInt(map.mapHeight), map.nodeGraph);
					indexOfCurrentPathDestination = 0;
			}
		}
		
		if (graphPath.getCount() > 0) { //(couldnt find a path, for example if the goal position unreachable)
			if (State.MOVING_TOWARDS_FIRST_NODE == movementState) { //move to first node of chosen path
				PositionIndexedNode firstNode = graphPath.get(0);
				if (moveTowards(firstNode.x, firstNode.y)) {
					movementState = State.TRAVERSING_PATH; //reached first node
				}
				
				
			} else if (State.TRAVERSING_PATH == movementState) {
				PositionIndexedNode currentNode = graphPath.get(indexOfCurrentPathDestination);
				
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
					indexOfCurrentPathDestination += 1;
					if (indexOfCurrentPathDestination < graphPath.getCount()) {
						currentNode = graphPath.get(indexOfCurrentPathDestination);
					} else {
						movementState = State.MOVING_TOWARDS_GOAL;
					}
				}
			} else if (movementState == State.MOVING_TOWARDS_GOAL) { //after finishing path, move short distance to goal pos
				movementState = null;
				/*if (moveTowards(goal.getX(), goal.getY())) { //reached goal
					movementState = null;
				}*/
			}
		} else { //there is no path, so trigger the choosing of a new random path and subsequent path generation to that point
			movementState = null;
		}
	}
	
	/**
	 *
	 * @param x goal x
	 * @param y goal y
	 * @return true if pos of Agent is close to goal x and y
	 */
	private boolean moveTowards(double x, double y) {
		double translateAmt = 0;
		if (notClose(pos.getX(), x)) {
			if (pos.getX() < x) {
				translateAmt = speed > x - pos.getX() ? x - pos.getX() : speed;
			} else if (pos.getX() > x) {
				translateAmt = speed > pos.getX() - x ? -(pos.getX() - x) : -speed;
			}
			pos.translate(translateAmt, 0);
		} else if (notClose(pos.getY(), y)){
			if (pos.getY() < y) {
				translateAmt = speed > y - pos.getY() ? y - pos.getY() : speed;
			} else if (pos.getY() > y) {
				translateAmt = speed > pos.getY() - y ? -(pos.getY() - y) : -speed;
			}
			pos.translate(0, translateAmt);
		}
		return closeTo(pos, new Point(x, y));
	}


	private boolean notClose(double x0, double x1) {
		return Math.abs(x0 - x1) > 0.00001;
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
