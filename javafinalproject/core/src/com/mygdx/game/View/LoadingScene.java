package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Kornkitt on 11/12/2015.
 */

public class LoadingScene implements Screen {
    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont font;
    private Texture loading;
    private Game game;

    private int barLoad;

    public LoadingScene(Game game){
        this.game = game;
        this.barLoad = 0;
    }

    @Override
    public void show() {
        font = new BitmapFont();
        loading = new Texture(Gdx.files.internal("loadingbar.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        font.setColor(Color.GREEN);
        font.draw(spriteBatch, "Loading . . .", 470, 320);
        spriteBatch.draw(loading, 50, 270, barLoad, 20);
        font.draw(spriteBatch, (int)(barLoad / 9.45) + " %", 485, 240);
        spriteBatch.end();

        barLoad += 1;
        if(barLoad >= 950){ game.setScreen(new MainMenuScreen(game)); }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
