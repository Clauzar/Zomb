package com.mygdx.game.Model;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Kornkitt on 11/13/2015.
 */
public class Killer extends Human{

    public static final float MAX_VELOCITY = 0.1f;

    public Killer(Vector2 position, int checkPos) {
        super(position, checkPos);
    }

    @Override
    public float getMAXVELOCITY() { return MAX_VELOCITY; }
}
