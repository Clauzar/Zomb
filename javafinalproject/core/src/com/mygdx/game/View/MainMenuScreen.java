package com.mygdx.game.View;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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

public class MainMenuScreen implements Screen, InputProcessor {

    public static boolean IS_MUSIC_PLAYING = true;
    public static boolean IS_SOUND_PLAYING = true;

    private SpriteBatch spriteBatch = new SpriteBatch();
    private Texture background;
    private Texture stopSound;
    private Game game;

    private Music bgSound;

    private int character;
    private boolean muteMusic = false;
    private boolean muteSound = false;
    private boolean wasPlayed = false;

    public MainMenuScreen(Game game){
        this.game = game;
        this.character = 1;

        bgSound = Gdx.audio.newMusic(Gdx.files.internal("GRIT_Watchful_Yeoman.wav"));
        if(IS_MUSIC_PLAYING) bgSound.setLooping(true); bgSound.play();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {
        stopSound   = new Texture(Gdx.files.internal("stop.png"));
        background  = new Texture(Gdx.files.internal("mainScreen.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0);
        if(!IS_MUSIC_PLAYING) spriteBatch.draw(stopSound, 940, 471, 80, 80);
        if(!IS_SOUND_PLAYING) spriteBatch.draw(stopSound, 950, 346, 80, 80);
        spriteBatch.end();
        if(!IS_MUSIC_PLAYING){ bgSound.setLooping(false); bgSound.stop(); }
        if(wasPlayed){
            bgSound.setLooping(false); bgSound.dispose();
            if(MainMenuScreen.IS_MUSIC_PLAYING) bgSound.stop();
            game.setScreen(new GameScreen(game, CharacterSelectionScreen.CHARACTER_SELECTED));
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
        if(screenX >= 940 && screenX <= 1030 && screenY >= 20 && screenY <= 95){
            if(!muteMusic) {
                IS_MUSIC_PLAYING = false;
                bgSound.stop(); bgSound.dispose();
                muteMusic = true;
            } else { muteMusic = false; bgSound.setLooping(true); bgSound.play(); IS_MUSIC_PLAYING = true; }
        } if(screenX >= 950 && screenX <= 1035 && screenY >= 125 && screenY <= 220){
            if(!muteSound) { IS_SOUND_PLAYING = false; muteSound = true; }
            else { muteSound = false; IS_SOUND_PLAYING = true; }
        } if(screenX >= 410 && screenX <= 620 && screenY >= 205 && screenY <= 305){
            wasPlayed = true;
            bgSound.stop(); bgSound.setLooping(false); bgSound.dispose();
        } if(screenX >= 315 && screenX <= 720 && screenY >= 325 && screenY <= 400){
            game.setScreen(new CharacterSelectionScreen(game));
        } if(screenX >= 415 && screenX <= 615 && screenY >= 415 && screenY <= 490){
            game.setScreen(new StageSelection(game));
        } if(screenX >= 375 && screenX <= 655 && screenY >= 510 && screenY <= 560){
            game.setScreen(new HowToPlay(game));
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
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
