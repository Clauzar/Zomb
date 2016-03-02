package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Kornkitt on 11/12/2015.
 */
public class StageSelection implements Screen, InputProcessor {

    public static String STAGE_SELECTED = "antique.tmx";

    private SpriteBatch spriteBatch = new SpriteBatch();
    private Texture background;
    private Game game;
    private int character = 0;
    private boolean wasTouched = false;
    private boolean stillTouched = false;

    public StageSelection(Game game){
        this.game = game;
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {
        background = new Texture(Gdx.files.internal("stageSelect.png"));
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0);
        spriteBatch.end();
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
        game.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!stillTouched){
            int temp = character;
            stillTouched = true;
            if(screenX <= 555){ STAGE_SELECTED = "arena.tmx"; character = 1;}
            else { STAGE_SELECTED = "antique.tmx"; character = 2;}
            if(temp == character) { game.setScreen(new MainMenuScreen(game)); }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        stillTouched = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
