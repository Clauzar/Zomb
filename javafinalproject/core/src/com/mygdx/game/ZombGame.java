package com.mygdx.game;

/**
 * Created by Kornkitt on 11/10/2015.
 */
import com.badlogic.gdx.Game;
import com.mygdx.game.View.GameScreen;
import com.mygdx.game.View.MainMenuScreen;
import com.mygdx.game.View.TopScreen;

public class ZombGame extends Game {

    @Override
    public void create() {
        setScreen(new TopScreen(this));
    }

}