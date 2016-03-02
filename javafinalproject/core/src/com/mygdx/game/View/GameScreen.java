package com.mygdx.game.View;

/**
 * Created by Kornkitt on 11/10/2015.
 */
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.Controller.WorldRenderer;
import com.mygdx.game.ZombGame;

public class GameScreen implements Screen {

    private WorldRenderer renderer;
    private Game game;

    public GameScreen(Game game, int character){
        this.game = game;
        renderer = new WorldRenderer(game, character);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        game.dispose();
    }
}
