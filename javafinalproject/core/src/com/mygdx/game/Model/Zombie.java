package com.mygdx.game.Model;

/**
 * Created by Kornkitt on 11/10/2015.
 */
import com.badlogic.gdx.math.Vector2;

public class Zombie {

    public static float MAX_VELOCITY = 2f;
    public static final float DAMPING = 0.87f;

    public enum State { Standing, Walking }

    private State state;
    private boolean facingRight;
    private boolean facingLeft;
    private boolean facingDown;
    private boolean facingUp;
    private float stateTime;
    private Vector2 position;
    private Vector2 velocity;

    private float width;
    private float height;
    private int lifeLimit;

    public Zombie(){
        position = new Vector2();
        velocity = new Vector2();
        state = State.Standing;
        facingRight = false;
        facingLeft = false;
        facingDown = true;
        facingUp = false;
        stateTime = 0;
        lifeLimit = 100;
    }

    /**************************** Getters/Setters ******************************/

    public void setHeight(float height) { this.height = height; }
    public void setWidth(float width) { this.width = width; }
    public float getHeight() { return height; }
    public float getWidth() { return width; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }
    public boolean isFacingLeft() {  return facingLeft; }
    public void setFacingLeft(boolean facingLeft) { this.facingLeft = facingLeft; }
    public boolean isFacingDown() { return facingDown; }
    public void setFacingDown(boolean facingDown) { this.facingDown = facingDown; }
    public boolean isFacingUp() { return facingUp; }
    public void setFacingUp(boolean facingUp) { this.facingUp = facingUp; }
    public float getStateTime() { return stateTime; }
    public void setStateTime(float stateTime) { this.stateTime = stateTime; }
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position = position; }
    public Vector2 getVelocity() { return velocity; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }
    public int getLifeLimit() { return lifeLimit; }
    public void setLifeLimit(int lifeLimit) { this.lifeLimit = lifeLimit; }

    public void setType(int type){
        if (type == 2) { setLifeLimit(150); Zombie.MAX_VELOCITY = 1f; }
        else if (type == 3) { setLifeLimit(50); Zombie.MAX_VELOCITY = 3f; }
        else if (type == 4) { setLifeLimit(75); Zombie.MAX_VELOCITY = 2.5f; }
        else if (type == 5) { setLifeLimit(125); Zombie.MAX_VELOCITY = 1.5f; }
        else if (type == 6) { setLifeLimit(120); Zombie.MAX_VELOCITY = 1.6f; }
        else if (type == 7) { setLifeLimit(80); Zombie.MAX_VELOCITY = 2.4f; }
        else if (type == 8) { setLifeLimit(20); Zombie.MAX_VELOCITY = 3.8f; }
    }

    public void update(float delta){
        stateTime += delta;
        position.add(velocity.cpy().scl(delta));
    }

}