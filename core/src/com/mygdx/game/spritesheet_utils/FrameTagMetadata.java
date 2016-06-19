package com.mygdx.game.spritesheet_utils;

/**
 * represents a frame tag metadata in the aseprite-generated spritesheet metadata
 * Frame tags are labels for ranges of frames
 * Frame tags = animations
 * @author elimonent
 *
 */
public class FrameTagMetadata {
	/**
	 * the frame tag's label
	 */
	protected String name;
	/**
	 * the first frame in the sequence labeled by this frame tag
	 */
	protected int from;
	/**
	 * the last frame in the sequence labeled by this frame tag
	 */
	protected int to;
}
