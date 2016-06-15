package com.mygdx.game.player;

import java.io.File;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.LineSeg;
import com.mygdx.game.ObjectShape;
import com.mygdx.game.Point;

/**
 * represents the shield class in-game
 * @author elimonent
 *
 */
public class ShieldClass extends Player {
	
	/*
	 * the shape of the shield class hitbox- mighht want to specify this in json later instead of hardcoding
	 */
	private static final ObjectShape shieldShape = new ObjectShape(Arrays.asList(
			new LineSeg(new Point(15, 0), new Point(15, 55)),
			new LineSeg(new Point(15, 55), new Point(50, 55)),
			new LineSeg(new Point(50, 55), new Point(50, 0)),
			new LineSeg(new Point(50, 0), new Point(15, 0))
			),
			new Point(0,0));
	
	public ShieldClass(int uid) {
		super(uid, shieldShape);
		/*
		 * TODO set the appearance based on the class
		 */
		changeAppearance(Gdx.files.internal("character_art" + File.separator + "ranger" + File.separator + "ranger_spritesheet.png"));
		Gdx.app.log(getClass().getSimpleName(), "instance of shield class created");
	}

	/**
	 * animation updates and stuff here
	 */
	@Override
	public void update(float stateTime) {
		// TODO Auto-generated method stub
		super.update(stateTime);	
	}

}
