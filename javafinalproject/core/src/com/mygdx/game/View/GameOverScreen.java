package com.mygdx.game.View;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Kornkitt on 11/12/2015.
 */

public class GameOverScreen implements Screen, InputProcessor {
    private SpriteBatch spriteBatch = new SpriteBatch();
    private Texture number;
    private Texture yourScoreIs;
    private Texture backToMainMenu;
    private BitmapFont font;
    private Game game;

    private static Preferences prefs;
    private Integer score;

    public GameOverScreen(Game game, int score){
        prefs = Gdx.app.getPreferences("HighScore");

        this.game = game;
        this.score = score;

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {
        font = new BitmapFont();
        number = new Texture(Gdx.files.internal("number/0.png"));
        yourScoreIs = new Texture(Gdx.files.internal("number/yourscoreis.png"));
        backToMainMenu = new Texture(Gdx.files.internal("number/backtomainmenu.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String checkScore = String.valueOf(score);
        float position = (float)(1066 / 2.0) - (float)(checkScore.length() * 197 / 2.0);

        spriteBatch.begin();
        spriteBatch.draw(yourScoreIs, 533 - (573 / 2), 360);
        for(int i = 0; i < checkScore.length(); i++) {
            number = new Texture(Gdx.files.internal("number/" + checkScore.charAt(i) + ".png"));
            spriteBatch.draw(number, position, 200);
            position += (197 / checkScore.length()) + (197 / (checkScore.length() * 2));
        } spriteBatch.draw(backToMainMenu, 170, 30);

        if(prefs.getInteger("HighScore") < score){
            prefs.putInteger("HighScore", score);
            prefs.flush();
        }
        font.draw(spriteBatch, "Highscore : " + prefs.getInteger("HighScore"), 455, 230);

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
        if(screenX >= 270 && screenY >= 470 && screenX <= 800 && screenY <= 535){
            game.setScreen(new MainMenuScreen(game));
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
