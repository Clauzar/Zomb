package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Kornkitt on 11/12/2015.
 */

public class TopScreen implements Screen {

    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont font;
    private Texture background;
    private Music bgSound;
    private Game game;

    public TopScreen(Game game){
        this.game = game;
    }

    @Override
    public void show() {
        background = new Texture(Gdx.files.internal("mainMenu.png"));
        bgSound = Gdx.audio.newMusic(Gdx.files.internal("GR_Main_Theme.wav"));
        bgSound.setLooping(true); bgSound.play();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0);
        font.setColor(Color.GREEN);
        font.draw(spriteBatch, "Touch Anywhere to Begin", 420, 100);
        spriteBatch.end();

        if(Gdx.input.justTouched()){
            bgSound.setLooping(false); bgSound.stop();
            game.setScreen(new LoadingScene(game));
        }

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
