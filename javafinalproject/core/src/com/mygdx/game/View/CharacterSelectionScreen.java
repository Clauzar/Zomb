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
public class CharacterSelectionScreen implements Screen, InputProcessor {

    public static int CHARACTER_SELECTED = 1;

    private SpriteBatch spriteBatch = new SpriteBatch();
    private Texture background;
    private Game game;
    private int character = 0;
    private String backgroundSelect = "characterSelect1.png";
    private boolean wasTouched = false;
    private boolean stillTouched = false;
    ShapeRenderer select = new ShapeRenderer();

    public CharacterSelectionScreen(Game game){
        this.game = game;
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {
        background = new Texture(Gdx.files.internal(backgroundSelect));
    }

    @Override
    public void render(float delta) {
        spriteBatch.begin();
        background = new Texture(Gdx.files.internal(backgroundSelect));
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
            stillTouched = true;

            if(screenX > 50 && screenX < 120 && screenY > 125 && screenY < 305){
                character = 1;
                wasTouched = true;
                backgroundSelect = "characterSelect1.png";
            } else if(screenX > 255 && screenX < 390 && screenY > 125 && screenY < 305){
                character = 2;
                wasTouched = true;
                backgroundSelect = "characterSelect2.png";
            } else if(screenX > 450 && screenX < 575 && screenY > 125 && screenY < 305){
                character = 3;
                wasTouched = true;
                backgroundSelect = "characterSelect3.png";
            } else if(screenX > 635 && screenX < 760 && screenY > 125 && screenY < 305){
                character = 4;
                wasTouched = true;
                backgroundSelect = "characterSelect4.png";
            } else if(screenX > 50 && screenX < 120 && screenY > 330 && screenY < 510){
                character = 5;
                wasTouched = true;
                backgroundSelect = "characterSelect5.png";
            } else if(screenX > 255 && screenX < 390 && screenY > 330 && screenY < 510){
                character = 6;
                wasTouched = true;
                backgroundSelect = "characterSelect6.png";
            } else if(screenX > 450 && screenX < 575 && screenY > 330 && screenY < 510){
                character = 7;
                wasTouched = true;
                backgroundSelect = "characterSelect7.png";
            } else if(screenX > 635 && screenX < 760 && screenY > 330 && screenY < 510){
                character = 8;
                wasTouched = true;
                backgroundSelect = "characterSelect8.png";
            }
        }

        if(screenX > 825 && screenY > 534){
            CHARACTER_SELECTED = character;
            game.setScreen(new MainMenuScreen(game));
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
