package com.mygdx.game;

public class GameObject {
    private boolean passable;
    protected Shape shape;

    public GameObject(Shape shape, boolean passable) {
        this.shape = shape;
        this.passable = passable;
    }
    public boolean isPassable() {
        return passable;
    }
    public Shape getShape() {
        return shape;
    }
    //returns true if the two objects take up some of the same space
    //like the middle of a venn diagram
    public boolean intersects(GameObject other) {
        return this.getShape().intersects(other.getShape());
    }
}
