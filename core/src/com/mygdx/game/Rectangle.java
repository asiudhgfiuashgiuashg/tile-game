package com.mygdx.game;

//representation of Rectangle based on walls
public class Rectangle {
	public double left, right, top, bottom;
	public Rectangle(double left, double right, double top, double bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	@Override
	public String toString() {
		return "left: " + this.left + " right:" + this.right + " top:" + this.top + " bottom:" + this.bottom;
	}
}
