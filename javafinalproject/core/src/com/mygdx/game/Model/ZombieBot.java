package com.mygdx.game.Model;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Kornkitt on 11/14/2015.
 */
public class ZombieBot extends Human {

    private static final float MAX_VELOCITY = 0.05f;

    public ZombieBot(Vector2 position, int checkPos) {
        super(position, checkPos);
    }

    @Override
    public float getMAXVELOCITY() { return MAX_VELOCITY; }
}
