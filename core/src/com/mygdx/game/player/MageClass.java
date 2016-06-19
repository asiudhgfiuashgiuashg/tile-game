package com.mygdx.game.player;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.LineSeg;
import com.mygdx.game.ObjectShape;
import com.mygdx.game.Point;
import com.mygdx.game.spritesheet_utils.SpritesheetMetadataParser;

/**
 * represents the mage class in-game
 * @author elimonent
 *
 */
public class MageClass extends Player {

	/*
	 * the shhape of thhe mage class hitbox - mighht want to specify this in json later instead of hardcoding
	 */
	private static final ObjectShape mageShape = new ObjectShape(Arrays.asList(
			new LineSeg(new Point(15, 0), new Point(15, 55)),
			new LineSeg(new Point(15, 55), new Point(50, 55)),
			new LineSeg(new Point(50, 55), new Point(50, 0)),
			new LineSeg(new Point(50, 0), new Point(15, 0))
			),
			new Point(0,0));
	
	public MageClass(int uid) {
		super(uid, mageShape);
		/*
		 * set the appearance based on the class
		 */
		setAppearance(Gdx.files.internal("character_art" + File.separator + "mage" + File.separator + "mage.png"));
		System.out.println ("instance of mage class created");
	}
	
	/**
	 * animation updates and stuff here
	 */
	@Override
	public void update(float stateTime) {
		// TODO Auto-generated method stub
		super.update(stateTime);	
	}
	
	/**
	 * the mage has extra animations because he has two different modes
	 * for now we'll just use the dark animations
	 */
	@Override
	public void setAppearance(FileHandle spritesheetFileHandle) {
		SpritesheetMetadataParser ssParser = new SpritesheetMetadataParser();
        Map<String, Animation> animations = ssParser.getAnimations(spritesheetFileHandle);
        for (String animationName: animations.keySet()) {
        	animations.get(animationName).setFrameDuration(ANIMATION_DURATION);
        }
        
        moveLeft = animations.get("left_walk_dark");
        idleLeft = idleRight = idleUp = new Animation(1, moveLeft.getKeyFrame(0));
        moveRight = animations.get("right_walk_dark");
        idleRight = idleUp = new Animation(1, moveRight.getKeyFrame(0));
        moveUp = animations.get("up_walk_dark");
        idleUp = new Animation(1, moveUp.getKeyFrame(0));
        moveDown = animations.get("down_walk_dark");
        idleDown = new Animation(1, moveDown.getKeyFrame(0));
        currentFrame = idleUp.getKeyFrame(0);
	}

}
