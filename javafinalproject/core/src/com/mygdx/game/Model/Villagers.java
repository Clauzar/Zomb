package com.mygdx.game.Model;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Kornkitt on 11/13/2015.
 */
public class Villagers extends Human {

    public static final float MAX_VELOCITY = 0.07f;

    public Villagers(Vector2 position, int checkPos) {
        super(position, checkPos);
    }

    @Override
    public float getMAXVELOCITY() { return MAX_VELOCITY; }
}
