package com.mygdx.game.player;

import java.io.File;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.LineSeg;
import com.mygdx.game.ObjectShape;
import com.mygdx.game.Point;

/**
 * represents the ranger class in-game
 */
public class RangerClass extends Player {

	/*
	 * thhe shape of thhe ranger clas hhitbox- mighht want to specify this in json later instead of hardcoding
	 */
	private static final ObjectShape rangerShape = new ObjectShape(Arrays.asList(
			new LineSeg(new Point(15, 0), new Point(15, 55)),
			new LineSeg(new Point(15, 55), new Point(50, 55)),
			new LineSeg(new Point(50, 55), new Point(50, 0)),
			new LineSeg(new Point(50, 0), new Point(15, 0))
			),
			new Point(0,0));
	
	public RangerClass(int uid) {
		super(uid, rangerShape);
		/*
		 * TODO set the appearance based on the class
		 */
		changeAppearance(Gdx.files.internal("character_art" + File.separator + "ranger" + File.separator + "ranger_spritesheet.png"));
		System.out.println("instance of ranger class created");
	}
	
	@Override
	protected void update(float stateTime) {
		// TODO Auto-generated method stub
		
	}

}
