package com.mygdx.game.Model;

/**
 * Created by Kornkitt on 11/12/2015.
 */
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Controller.WorldRenderer;

public abstract class Human {

    public enum State { Standing, Walking }

    //public static final float MAX_VELOCITY = 0.1f;
    private static final float DAMPING = 1.6f;

    private State state;
    private boolean facingRight;
    private boolean facingLeft;
    private boolean facingUp;
    private boolean facingDown;
    private float stateTime;
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private Rectangle bounds;

    private float width;
    private float height;
    private int checkPos;
    private int steps;
    private int lifeLimit;

    public Human(Vector2 position, int checkPos){
        this.position = position;
        this.checkPos = checkPos;

        bounds = new Rectangle();
        this.bounds.width = 0.87f;
        this.bounds.height = 1.07f;
        this.bounds.x = this.position.x;
        this.bounds.y = this.position.y;

        velocity = new Vector2();
        velocity.x = -Zombie.MAX_VELOCITY;
        acceleration = new Vector2();
        state = State.Walking;
        facingRight = false;
        facingDown = true;
        facingUp = false;
        facingLeft = false;
        stateTime = 0;
        steps = 0;
        lifeLimit = 100;
        width = 32;
        height = 32;
    }

    /**************************************************** Getters/Setters *******************************************************/

    public Vector2 getAcceleration() {
        return acceleration;
    }
    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public void setWidth(float width) {
        this.width = width;
    }
    public float getHeight() {
        return height;
    }
    public float getWidth() {
        return width;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public boolean isFacingRight() {
        return facingRight;
    }
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    public boolean isFacingLeft() {
        return facingLeft;
    }
    public void setFacingLeft(boolean facingLeft) {
        this.facingLeft = facingLeft;
    }
    public boolean isFacingUp() {
        return facingUp;
    }
    public void setFacingUp(boolean facingUp) {
        this.facingUp = facingUp;
    }
    public boolean isFacingDown() {
        return facingDown;
    }
    public void setFacingDown(boolean facingDown) {
        this.facingDown = facingDown;
    }
    public float getStateTime() {
        return stateTime;
    }
    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }
    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
        this.bounds.width = 32;
        this.bounds.height = 32;
        this.bounds.x = this.position.x;
        this.bounds.y = this.position.y;
    }
    public Vector2 getVelocity() {
        return velocity;
    }
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    public int getCheckPos() { return checkPos; }
    public void setCheckPos(int i) { this.checkPos = i; }
    public int getSteps() { return steps; }
    public void increaseSteps() { this.steps++; }
    public void resetStep() { this.steps = 0; }
    public int getLifeLimit() { return lifeLimit; }
    public void setLifeLimit(int lifeLimit) { this.lifeLimit = lifeLimit; }
    public abstract float getMAXVELOCITY();
    public Rectangle getBounds() { return bounds; }
}