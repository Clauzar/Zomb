package com.mygdx.game.Controller;

/**
 * Created by Kornkitt on 11/10/2015.
 */
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Model.*;
import com.mygdx.game.View.GameOverScreen;
import com.mygdx.game.View.MainMenuScreen;
import com.mygdx.game.View.StageSelection;

import java.util.Iterator;
import java.util.Random;


public class WorldRenderer implements InputProcessor{

    private static final float UNIT_SCALE = 1/16f;
    private static final float RUNNING_FRAME_DURATION = 0.09f;
    private static final float RUNNING_FRAME_DURATION_HUMAN = 0.2f;

    private static final float CAMERA_WIDTH = 10f;
    private static final float CAMERA_HEIGHT = 7f;

    public static int width, height;
    private int character;
    private int score = 0;
    private boolean isHumanDied = false;

    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private Level level;
    private Zombie zombie;

    private Batch batch;
    private SpriteBatch spriteBatch = new SpriteBatch();
    private BitmapFont font = new BitmapFont();

    /* Textures for Player */
    private TextureRegion playerIdleLeft;
    private TextureRegion playerIdleRight;
    private TextureRegion playerIdleUp;
    private TextureRegion playerIdleDown;

    private TextureRegion villagersIdleLeft;
    private TextureRegion villagersIdleRight;
    private TextureRegion villagersIdleUp;
    private TextureRegion villagersIdleDown;

    private TextureRegion killersIdleLeft;
    private TextureRegion killersIdleRight;
    private TextureRegion killersIdleUp;
    private TextureRegion killersIdleDown;

    private TextureRegion playerFrame;
    private TextureRegion humanFrame;

    private Array<Human> humanList;
    private Array<Human> humanToRemove;

    /* Animations for Player */
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private Animation walkUpAnimation;
    private Animation walkDownAnimation;

    private Animation walkLeftVillagersAnimation;
    private Animation walkRightVillagersAnimation;
    private Animation walkUpVillagersAnimation;
    private Animation walkDownVillagersAnimation;

    private Animation walkLeftKillerAnimation;
    private Animation walkRightKillerAnimation;
    private Animation walkUpKillerAnimation;
    private Animation walkDownKillerAnimation;

    private Texture leftController;
    private Texture rightController;
    private Texture upController;
    private Texture downController;

    private Texture redFace;
    private Texture greyFace;

    private Music siren;
    private Music bgSound;
    private Sound eating;
    private Sound hit;

    private int checkFace = 0;

    //xy coordinate
    int x;
    int y;

    /* for debug rendering */
    ShapeRenderer debugRenderer = new ShapeRenderer();

    private Array<Rectangle> tiles;
    private Array<Rectangle> enemyTiles;
    private Array<Rectangle> zombieBotTiles;

    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject () {
            return new Rectangle();
        }
    };

    private Game game;

    public WorldRenderer(Game game, int character) {
        this.game = game;
        this.character = character;

        level = new Level(StageSelection.STAGE_SELECTED); //run by android
        //level = new Level("android/assets/mapp.tmx"); //run by desktop
        zombie = new Zombie();
        humanList = new Array<Human>();
        humanToRemove = new Array<Human>();

        loadArrowController();
        loadPlayerTextures();
        loadHumanTexture();

        tiles = new Array<Rectangle>();
        enemyTiles = new Array<Rectangle>();
        zombieBotTiles = new Array<Rectangle>();

        Gdx.input.setInputProcessor(this);

        zombie.setPosition(new Vector2(16, 10));
        zombie.setWidth( UNIT_SCALE * playerIdleDown.getRegionWidth());
        zombie.setHeight( UNIT_SCALE * playerIdleDown.getRegionHeight());

        loadHuman();

        renderer = new OrthogonalTiledMapRenderer(level.getMap(), UNIT_SCALE);

        batch = renderer.getBatch();

        siren   = Gdx.audio.newMusic(Gdx.files.internal("Police Siren_5.wav"));
        bgSound = Gdx.audio.newMusic(Gdx.files.internal("action1.wav"));
        eating  = Gdx.audio.newSound(Gdx.files.internal("punch.wav"));
        hit     = Gdx.audio.newSound(Gdx.files.internal("knife_hit4.wav"));
        siren.setLooping(true); bgSound.setLooping(true);
        if(MainMenuScreen.IS_MUSIC_PLAYING) bgSound.play();

        this.camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.camera.setToOrtho(false,CAMERA_WIDTH,CAMERA_HEIGHT);
        this.camera.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f, 0);
        this.camera.update();

    }

    public void render (float delta) {

        debugRenderer.setProjectionMatrix(camera.combined);

        renderer.setView(camera);
        moveCamera(zombie.getPosition().x, zombie.getPosition().y);
        camera.update();

        renderer.render();

        batch.begin();
        drawPlayer();
        drawHuman(delta);
        if(zombie.getLifeLimit() < 16){
            if(MainMenuScreen.IS_SOUND_PLAYING) siren.play();
            checkFace++;
            batch.draw(redFace, 0, 0, 30, 20);
        } if(checkFace % 11 == 2 || checkFace % 11 == 3 || checkFace % 11 == 4){
            batch.draw(greyFace, 0, 0, 30, 20);
        }
        batch.end();

        spriteBatch.begin();
        drawArrowController();
        font.draw(spriteBatch, "Score : " + score, 50, 530);
        font.draw(spriteBatch, "Life Limit : " + zombie.getLifeLimit(), 50, 500);
        spriteBatch.end();

        drawDebug();
        updatePlayer(delta);
        zombie.update(delta);

    }

    public void moveCamera(float x,float y){
        if ((zombie.getPosition().x > CAMERA_WIDTH / 2)) {
            camera.position.set(x, y, 0);
            camera.update();
        }
    }

    private void setFacing(Zombie zombie, boolean left, boolean right, boolean up, boolean down){
        zombie.setFacingLeft(left);
        zombie.setFacingRight(right);
        zombie.setFacingUp(up);
        zombie.setFacingDown(down);
    }

    private void setFacing(Human human, boolean left, boolean right, boolean up, boolean down){
        human.setFacingLeft(left);
        human.setFacingRight(right);
        human.setFacingUp(up);
        human.setFacingDown(down);
    }

    public void updatePlayer(float delta){

        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            zombie.getVelocity().x = -Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, true, false, false, false);
        } if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            zombie.getVelocity().x = Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, true, false, false);
        } if (Gdx.input.isKeyPressed(Keys.UP)) {
            zombie.getVelocity().y = Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, false, true, false);
        } if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            zombie.getVelocity().y = -Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, false, false, true);
        }

        if(!Gdx.input.isKeyPressed(Keys.UP) && !Gdx.input.isKeyPressed(Keys.DOWN)
                && !Gdx.input.isKeyPressed(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.RIGHT)){
            zombie.setState(Zombie.State.Standing);
        }


        if(Gdx.input.isTouched()){
            if(Gdx.input.getX() <= 70 && Gdx.input.getY() >= 426 && Gdx.input.getY() <= 496){
                zombie.getVelocity().x = -Zombie.MAX_VELOCITY;
                zombie.setState(Zombie.State.Walking);
                setFacing(zombie, true, false, false, false);
            } if(Gdx.input.getX() >= 140 && Gdx.input.getX() <= 210 && Gdx.input.getY() >= 426 && Gdx.input.getY() <= 496){
                zombie.getVelocity().x = Zombie.MAX_VELOCITY;
                zombie.setState(Zombie.State.Walking);
                setFacing(zombie, false, true, false, false);
            } if(Gdx.input.getX() >= 70 && Gdx.input.getX() <= 140 && Gdx.input.getY() >= 356 && Gdx.input.getY() <= 426){
                zombie.getVelocity().y = Zombie.MAX_VELOCITY;
                zombie.setState(Zombie.State.Walking);
                setFacing(zombie, false, false, true, false);
            } if(Gdx.input.getX() >= 70 && Gdx.input.getX() <= 140 && Gdx.input.getY() >= 496){
                zombie.getVelocity().y = -Zombie.MAX_VELOCITY;
                zombie.setState(Zombie.State.Walking);
                setFacing(zombie, false, false, false, true);
            }
        }

        if(!Gdx.input.isTouched()) zombie.setState(Zombie.State.Standing);

        if (Math.abs(zombie.getVelocity().x) > Zombie.MAX_VELOCITY) {
            zombie.getVelocity().x = Math.signum(zombie.getVelocity().x) * zombie.MAX_VELOCITY;
        } if (Math.abs(zombie.getVelocity().y) > Zombie.MAX_VELOCITY) {
            zombie.getVelocity().y = Math.signum(zombie.getVelocity().y) * zombie.MAX_VELOCITY;
        }

        if (Math.abs(zombie.getVelocity().x) < 1) { zombie.getVelocity().x = 0; }
        if (Math.abs(zombie.getVelocity().y) < 1) { zombie.getVelocity().y = 0; }

        if(zombie.getPosition().x < 0) zombie.setPosition(new Vector2(0, zombie.getPosition().y));
        if(zombie.getPosition().y < 0) zombie.setPosition(new Vector2(zombie.getPosition().x, 0));
        if(zombie.getPosition().x > 28) zombie.setPosition(new Vector2(28, zombie.getPosition().y));
        if(zombie.getPosition().y > 18) zombie.setPosition(new Vector2(zombie.getPosition().x, 18));

        zombie.getVelocity().scl(delta);

        Rectangle playerRect = rectPool.obtain();
        playerRect.set(zombie.getPosition().x, zombie.getPosition().y, zombie.getWidth(), zombie.getHeight());
        getHumanTile();

        Iterator<Rectangle> iter = enemyTiles.iterator();
        int index = 0;
        while (iter.hasNext()){
            Rectangle human = iter.next();
            Random rand = new Random();
            if(human.overlaps(playerRect)){
                if(humanList.get(index).getClass().getSimpleName().equalsIgnoreCase("Killer")) {
                    if(MainMenuScreen.IS_SOUND_PLAYING) hit.play();
                    humanList.removeIndex(index); score -= 3;
                    iter.remove(); zombie.setLifeLimit(zombie.getLifeLimit() - rand.nextInt(15) + 1);
                } else if(humanList.get(index).getClass().getSimpleName().equalsIgnoreCase("Villagers")){
                    if(MainMenuScreen.IS_SOUND_PLAYING) eating.play();
                    humanList.set(index, new ZombieBot(new Vector2 (humanList.get(index).getPosition().x,
                            humanList.get(index).getPosition().y), humanList.get(index).getCheckPos()));
                    humanList.get(index).setWidth(0.87f);
                    humanList.get(index).setHeight(1.07f);
                    score += 10; addHuman();
                }
            } index++;
        }
        if(score <= 0) { score = 0; }
        if(zombie.getLifeLimit() <= 0){
            siren.setLooping(false);
            if(MainMenuScreen.IS_SOUND_PLAYING) siren.stop();
            if(MainMenuScreen.IS_MUSIC_PLAYING) bgSound.stop();
            game.setScreen(new GameOverScreen(game, score));
        }


        // unscale the velocity by the inverse delta time and set
        // the latest position
        zombie.getPosition().add(zombie.getVelocity());
        zombie.getVelocity().scl(1 / delta);

        zombie.getVelocity().x *= Zombie.DAMPING;
        zombie.setStateTime(zombie.getStateTime() + delta);
        zombie.getVelocity().y *= Zombie.DAMPING;
    }

    public void loadArrowController(){
        upController    = new Texture(Gdx.files.internal("upArrow.png"));
        downController  = new Texture(Gdx.files.internal("downArrow.png"));
        leftController  = new Texture(Gdx.files.internal("leftArrow.png"));
        rightController = new Texture(Gdx.files.internal("rightArrow.png"));
    }

    public void loadPlayerTextures(){
        Texture atlas = new Texture(Gdx.files.internal("ZombieSheet.png")); //run by android
        //Texture atlas = new Texture(Gdx.files.internal("android/assets/ZombieSheet.png")); //run by desktop

        int atlasX = 0, atlasY = 0;
        if(character == 2) { atlasX = 96 * 3; atlasY = atlas.getHeight() / 2; }
        if(character == 3)                    atlasY = atlas.getHeight() / 2;
        if(character == 4)   atlasX = 96;
        if(character == 5)   atlasX = 96 * 3;
        if(character == 6) { atlasX = 96;     atlasY = atlas.getHeight() / 2; }
        if(character == 7)   atlasX = 96 * 2;
        if(character == 8) { atlasX = 96 * 2; atlasY = atlas.getHeight() / 2; }

		/* Standing */
        playerIdleDown  = new TextureRegion(atlas, 32 + atlasX, 0  + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        playerIdleUp    = new TextureRegion(atlas, 32 + atlasX, 96 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        playerIdleRight = new TextureRegion(atlas, 32 + atlasX, 64 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        playerIdleLeft  = new TextureRegion(atlas, 32 + atlasX, 32 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);

        TextureRegion[] walkingDown     = new TextureRegion[3];
        TextureRegion[] walkingLeft     = new TextureRegion[3];
        TextureRegion[] walkingRight    = new TextureRegion[3];
        TextureRegion[] walkingUp       = new TextureRegion[3];
        for(int i = 0, j = 0; j < 3; i += 32, j++){
            walkingDown[j]  = new TextureRegion(atlas, i + atlasX, 0  + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            walkingUp[j]    = new TextureRegion(atlas, i + atlasX, 96 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            walkingRight[j] = new TextureRegion(atlas, i + atlasX, 64 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            walkingLeft[j]  = new TextureRegion(atlas, i + atlasX, 32 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        }

        walkLeftAnimation   = new Animation(RUNNING_FRAME_DURATION, walkingLeft);
        walkRightAnimation  = new Animation(RUNNING_FRAME_DURATION, walkingRight);
        walkDownAnimation   = new Animation(RUNNING_FRAME_DURATION, walkingDown);
        walkUpAnimation     = new Animation(RUNNING_FRAME_DURATION, walkingUp);

        redFace = new Texture(Gdx.files.internal("redFace.png"));
        greyFace = new Texture(Gdx.files.internal("greyFace.png"));
    }

    public void loadHumanTexture(){
        loadKillerTexture();
        loadVillagersTexture();
    }

    public void loadVillagersTexture(){
        Texture atlas = new Texture(Gdx.files.internal("villagers.png"));

        int atlasX = 0, atlasY = 0;
        if(character == 2) { atlasX = 96 * 3; atlasY = atlas.getHeight() / 2; }
        if(character == 3)                    atlasY = atlas.getHeight() / 2;
        if(character == 4)   atlasX = 96;
        if(character == 5)   atlasX = 96 * 3;
        if(character == 6) { atlasX = 96;     atlasY = atlas.getHeight() / 2; }
        if(character == 7)   atlasX = 96 * 2;
        if(character == 8) { atlasX = 96 * 2; atlasY = atlas.getHeight() / 2; }

        villagersIdleDown  = new TextureRegion(atlas, 32 + atlasX, 0  + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        villagersIdleUp    = new TextureRegion(atlas, 32 + atlasX, 96 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        villagersIdleRight = new TextureRegion(atlas, 32 + atlasX, 64 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        villagersIdleLeft  = new TextureRegion(atlas, 32 + atlasX, 32 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);

        TextureRegion[] villagersWalkingDown     = new TextureRegion[3];
        TextureRegion[] villagersWalkingLeft     = new TextureRegion[3];
        TextureRegion[] villagersWalkingRight    = new TextureRegion[3];
        TextureRegion[] villagersWalkingUp       = new TextureRegion[3];
        for(int i = 0, j = 0; j < 3; i += 32, j++){
            villagersWalkingDown[j]  = new TextureRegion(atlas, i + atlasX, 0  + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            villagersWalkingUp[j]    = new TextureRegion(atlas, i + atlasX, 96 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            villagersWalkingRight[j] = new TextureRegion(atlas, i + atlasX, 64 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            villagersWalkingLeft[j]  = new TextureRegion(atlas, i + atlasX, 32 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        }

        walkLeftVillagersAnimation   = new Animation(RUNNING_FRAME_DURATION_HUMAN, villagersWalkingLeft);
        walkRightVillagersAnimation  = new Animation(RUNNING_FRAME_DURATION_HUMAN, villagersWalkingRight);
        walkDownVillagersAnimation   = new Animation(RUNNING_FRAME_DURATION_HUMAN, villagersWalkingDown);
        walkUpVillagersAnimation     = new Animation(RUNNING_FRAME_DURATION_HUMAN, villagersWalkingUp);
    }

    public void loadKillerTexture(){
        Texture atlas = new Texture(Gdx.files.internal("killerTexture.png"));

        zombie.setType(character);

        int atlasX = 0, atlasY = 0;
        if(character == 2) { atlasX = 96 * 3; atlasY = atlas.getHeight() / 2; }
        if(character == 3)                    atlasY = atlas.getHeight() / 2;
        if(character == 4)   atlasX = 96;
        if(character == 5)   atlasX = 96 * 3;
        if(character == 6) { atlasX = 96;     atlasY = atlas.getHeight() / 2; }
        if(character == 7)   atlasX = 96 * 2;
        if(character == 8) { atlasX = 96 * 2; atlasY = atlas.getHeight() / 2; }

        killersIdleDown  = new TextureRegion(atlas, 32 + atlasX, 0  + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        killersIdleUp    = new TextureRegion(atlas, 32 + atlasX, 96 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        killersIdleRight = new TextureRegion(atlas, 32 + atlasX, 64 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        killersIdleLeft  = new TextureRegion(atlas, 32 + atlasX, 32 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);

        TextureRegion[] killersWalkingDown     = new TextureRegion[3];
        TextureRegion[] killersWalkingLeft     = new TextureRegion[3];
        TextureRegion[] killersWalkingRight    = new TextureRegion[3];
        TextureRegion[] killersWalkingUp       = new TextureRegion[3];
        for(int i = 0, j = 0; j < 3; i += 32, j++){
            killersWalkingDown[j]  = new TextureRegion(atlas, i + atlasX, 0  + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            killersWalkingUp[j]    = new TextureRegion(atlas, i + atlasX, 96 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            killersWalkingRight[j] = new TextureRegion(atlas, i + atlasX, 64 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
            killersWalkingLeft[j]  = new TextureRegion(atlas, i + atlasX, 32 + atlasY, atlas.getWidth() / 12, atlas.getHeight() / 8);
        }

        walkLeftKillerAnimation   = new Animation(RUNNING_FRAME_DURATION_HUMAN, killersWalkingLeft);
        walkRightKillerAnimation  = new Animation(RUNNING_FRAME_DURATION_HUMAN, killersWalkingRight);
        walkDownKillerAnimation   = new Animation(RUNNING_FRAME_DURATION_HUMAN, killersWalkingDown);
        walkUpKillerAnimation     = new Animation(RUNNING_FRAME_DURATION_HUMAN, killersWalkingUp);
    }

    public void loadHuman(){
        Random rand = new Random();
        int enemy = rand.nextInt(3) + 2;
        int checkType = 0;
        Human[] humans = new Human[enemy];
        for(int i = 0; i < enemy; i++){
            int checkPos = rand.nextInt(4);
            float randY = rand.nextInt(19) + rand.nextFloat();
            float randX = rand.nextInt(29) + rand.nextFloat();
            if(checkPos == 0 && checkType == 0) humans[i] = new Villagers(new Vector2(-1, randY), 0);
            if(checkPos == 1 && checkType == 0) humans[i] = new Villagers(new Vector2(randX, -1), 1);
            if(checkPos == 2 && checkType == 0) humans[i] = new Villagers(new Vector2(29, randY), 2);
            if(checkPos == 3 && checkType == 0) humans[i] = new Villagers(new Vector2(randX, 19), 3);
            if(checkPos == 0 && checkType == 1) humans[i] = new Killer(new Vector2(-1, randY), 0);
            if(checkPos == 1 && checkType == 1) humans[i] = new Killer(new Vector2(randX, -1), 1);
            if(checkPos == 2 && checkType == 1) humans[i] = new Killer(new Vector2(29, randY), 2);
            if(checkPos == 3 && checkType == 1) humans[i] = new Killer(new Vector2(randX, 19), 3);
            humans[i].setWidth(zombie.getWidth());
            humans[i].setHeight(zombie.getHeight());
            humans[i].setState(Human.State.Walking);
            humanList.add(humans[i]);
            checkType = rand.nextInt(2);
        }
    }

    public void addHuman(){
        Random rand = new Random();
        int enemy = rand.nextInt(3) + 1;
        int checkType = 0;
        Human[] humans = new Human[enemy];
        for(int i = 0; i < enemy; i++){
            int checkPos = rand.nextInt(4);
            float randY = rand.nextInt(19) + rand.nextFloat();
            float randX = rand.nextInt(29) + rand.nextFloat();
            if(checkPos == 0 && checkType == 0) humans[i] = new Villagers(new Vector2(-1, randY), 0);
            if(checkPos == 1 && checkType == 0) humans[i] = new Villagers(new Vector2(randX, -1), 1);
            if(checkPos == 2 && checkType == 0) humans[i] = new Villagers(new Vector2(29, randY), 2);
            if(checkPos == 3 && checkType == 0) humans[i] = new Villagers(new Vector2(randX, 19), 3);
            if(checkPos == 0 && checkType == 1) humans[i] = new Killer(new Vector2(-1, randY), 0);
            if(checkPos == 1 && checkType == 1) humans[i] = new Killer(new Vector2(randX, -1), 1);
            if(checkPos == 2 && checkType == 1) humans[i] = new Killer(new Vector2(29, randY), 2);
            if(checkPos == 3 && checkType == 1) humans[i] = new Killer(new Vector2(randX, 19), 3);
            humans[i].setWidth(zombie.getWidth());
            humans[i].setHeight(zombie.getHeight());
            humans[i].setState(Human.State.Walking);
            humanList.add(humans[i]);
            checkType = rand.nextInt(2);
        }
    }

    public void getHumanTile(){
        enemyTiles.clear();
        for(Human human : humanList) {
            Rectangle humanRect = new Rectangle();
            humanRect.set(human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
            enemyTiles.add(humanRect);
        }
    }

    public void drawArrowController(){
        spriteBatch.draw(upController,     70, 140, 70, 70);
        spriteBatch.draw(downController,   70,   0, 70, 70);
        spriteBatch.draw(rightController, 140,  70, 70, 70);
        spriteBatch.draw(leftController,    0,  70, 70, 70);
    }

    public void drawPlayer(){
        if(zombie.isFacingDown())   playerFrame = playerIdleDown;
        if(zombie.isFacingLeft())   playerFrame = playerIdleLeft;
        if(zombie.isFacingRight())  playerFrame = playerIdleRight;
        if(zombie.isFacingUp())     playerFrame = playerIdleUp;
        if(zombie.getState() == Zombie.State.Walking){
            if(zombie.isFacingDown())   playerFrame = walkDownAnimation.getKeyFrame(zombie.getStateTime(), true);
            if(zombie.isFacingLeft())   playerFrame = walkLeftAnimation.getKeyFrame(zombie.getStateTime(), true);
            if(zombie.isFacingRight())  playerFrame = walkRightAnimation.getKeyFrame(zombie.getStateTime(), true);
            if(zombie.isFacingUp())     playerFrame = walkUpAnimation.getKeyFrame(zombie.getStateTime(), true);
        }
        batch.draw(playerFrame, zombie.getPosition().x, zombie.getPosition().y, zombie.getWidth(), zombie.getHeight());


    }

    public void drawHuman(float delta){
        Random rand = new Random();
        int index = 0;
        for(Human human : humanList){
            if(human.isFacingDown() && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))
                humanFrame = human.getClass().getSimpleName().equalsIgnoreCase("Villagers") ?
                    walkDownVillagersAnimation.getKeyFrame(human.getStateTime(), true) : walkDownKillerAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingLeft() && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))
                humanFrame = human.getClass().getSimpleName().equalsIgnoreCase("Villagers") ?
                    walkLeftVillagersAnimation.getKeyFrame(human.getStateTime(), true) : walkLeftKillerAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingRight() && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))
                humanFrame = human.getClass().getSimpleName().equalsIgnoreCase("Villagers") ?
                    walkRightVillagersAnimation.getKeyFrame(human.getStateTime(), true) : walkRightKillerAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingUp() && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))
                humanFrame = human.getClass().getSimpleName().equalsIgnoreCase("Villagers") ?
                    walkUpVillagersAnimation.getKeyFrame(human.getStateTime(), true) : walkUpKillerAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingDown() && human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))    humanFrame = walkDownAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingLeft() && human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))    humanFrame = walkLeftAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingRight() && human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))   humanFrame = walkRightAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.isFacingUp() && human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot"))      humanFrame = walkUpAnimation.getKeyFrame(human.getStateTime(), true);
            if(human.getCheckPos() == 0 && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot")) {
                setFacing(human, false, true, false, false);
                human.setPosition(new Vector2(human.getPosition().x + human.getMAXVELOCITY(), human.getPosition().y));
                if(human.getPosition().x > 28) {
                    human.setCheckPos(rand.nextInt(4));
                } batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
            } else if(human.getCheckPos() == 1 && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot")) {
                setFacing(human, false, false, true, false);
                human.setPosition(new Vector2(human.getPosition().x, human.getPosition().y +  human.getMAXVELOCITY()));
                if(human.getPosition().y > 18) {
                    human.setCheckPos(rand.nextInt(4));
                } batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
            } else if(human.getCheckPos() == 2 && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot")) {
                setFacing(human, true, false, false, false);
                human.setPosition(new Vector2(human.getPosition().x -  human.getMAXVELOCITY(), human.getPosition().y));
                if(human.getPosition().x <= 0) {
                    human.setCheckPos(rand.nextInt(4));
                } batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
            } else if(human.getCheckPos() == 3 && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot")) {
                setFacing(human, false, false, false, true);
                human.setPosition(new Vector2(human.getPosition().x, human.getPosition().y -  human.getMAXVELOCITY()));
                if(human.getPosition().y <= 0) {
                    human.setCheckPos(rand.nextInt(4));
                } batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
            } if(human.getSteps() > rand.nextInt(50) + 50){
                human.resetStep();
                human.setCheckPos(rand.nextInt(4));
            } human.increaseSteps();
            if(human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot")) {
                if (human.getCheckPos() == 0) {
                    setFacing(human, false, true, false, false);
                    human.setPosition(new Vector2(human.getPosition().x + human.getMAXVELOCITY(), human.getPosition().y));
                    batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
                } else if (human.getCheckPos() == 1) {
                    setFacing(human, false, false, true, false);
                    human.setPosition(new Vector2(human.getPosition().x, human.getPosition().y + human.getMAXVELOCITY()));
                    batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
                } else if (human.getCheckPos() == 2) {
                    setFacing(human, true, false, false, false);
                    human.setPosition(new Vector2(human.getPosition().x - human.getMAXVELOCITY(), human.getPosition().y));
                    batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
                } else if (human.getCheckPos() == 3) {
                    setFacing(human, false, false, false, true);
                    human.setPosition(new Vector2(human.getPosition().x, human.getPosition().y - human.getMAXVELOCITY()));
                    batch.draw(humanFrame, human.getPosition().x, human.getPosition().y, human.getWidth(), human.getHeight());
                } if(human.getPosition().x < 0 || human.getPosition().y < 0 || human.getPosition().x > 29 || human.getPosition().y > 19){
                    humanList.removeIndex(index); addHuman();
                }
            } if(human.getSteps() > rand.nextInt(50) + 50 && !human.getClass().getSimpleName().equalsIgnoreCase("ZombieBot")){
                human.resetStep();
                human.setCheckPos(rand.nextInt(4));
            } human.increaseSteps();
            human.setStateTime(human.getStateTime() + delta);
            index++;
        }


    }

    public void drawDebug(){

        debugRenderer.begin(ShapeType.Line);

        Rectangle rect = new Rectangle(0, 0, zombie.getWidth(), zombie.getHeight());
        float x1 = zombie.getPosition().x + rect.x;
        float y1 = zombie.getPosition().y + rect.y;
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(x1, y1, rect.width, rect.height);
        debugRenderer.end();

    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Keys.LEFT) {
            zombie.getVelocity().x = -Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, true, false, false, false);
        } if (keycode == Keys.RIGHT) {
            zombie.getVelocity().x = Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, true, false, false);
        } if (keycode == Keys.UP) {
            zombie.getVelocity().y = Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, false, true, false);
        } if (keycode == Keys.DOWN) {
            zombie.getVelocity().y = -Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, false, false, true);
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Keys.UP || keycode == Keys.DOWN
                || keycode == Keys.LEFT || keycode == Keys.RIGHT){
            zombie.setState(Zombie.State.Standing);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(screenX <= 70 && screenY >= 426 && screenY <= 496){
            zombie.getVelocity().x = -Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, true, false, false, false);
        } if(screenX >= 140 && screenX <= 210 && screenY >= 426 && screenY <= 496){
            zombie.getVelocity().x = Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, true, false, false);
        } if(screenX >= 70 && screenX <= 140 && screenY >= 356 && screenY <= 426){
            zombie.getVelocity().y = Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, false, true, false);
        } if(screenX >= 70 && screenX <= 140 && screenY >= 496){
            zombie.getVelocity().y = -Zombie.MAX_VELOCITY;
            zombie.setState(Zombie.State.Walking);
            setFacing(zombie, false, false, false, true);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        zombie.setState(Zombie.State.Standing);
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
