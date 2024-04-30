package inf112.skeleton.app.scene;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.compression.lzma.Base;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.controller.EnemyController;
import inf112.skeleton.app.controller.MouseController;
import inf112.skeleton.app.controller.TowerController;
import inf112.skeleton.app.enums.DefenderType;
import inf112.skeleton.app.level.Level;
//import inf112.skeleton.app.scene.WorldController;
import inf112.skeleton.app.tower.BaseDefender;
import inf112.skeleton.app.ui.menu.MainControlMenu;
import inf112.skeleton.app.util.GameConstants;
import inf112.skeleton.app.util.GameSettings;
import inf112.skeleton.app.util.MusicManager;


import static inf112.skeleton.app.util.GameConstants.*;

public class PlayScene extends AbstractGameScene {
    private boolean paused;
    private Stage stage;
    private Skin uimenuskin;

    //Buttons
    private Button gunnerButton;
    private Button sniperButton;
    private Button bomberButton;
    private Button doubleSpeedButton;
    private Button pauseButton;
    private Button exitButton;
    private Button speedUpgradeButton;
    private Button damageUpgradeButton;
    private Button rangeUpgradeButton;
    private Button removeDefenderButton;

    private boolean isToggledSpeed = false;
    private boolean isToggledPause = false;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Level level;
    private EnemyController enemyController;
    private TowerController towerController;
    private OrthographicCamera camera;
    private CameraManager cameraManager;
    private MainControlMenu controlMenu;
    private BitmapFont bitmapFont;

    private TextureAtlas towerAtlas;

    public PlayScene(Game game, int mapNumber) {
        super(game);
        initializeResources();
        setupUI();
        setupInput(); // Now setupInput can be called safely
        this.level = new Level(game, mapNumber);
        initializeGameControllers();

    }

    private void initializeResources() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        towerAtlas = new TextureAtlas("zombie.atlas");
        stage = new Stage(new ScreenViewport(camera), spriteBatch);


    }

    private void setupInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                System.out.println("Key Pressed: " + Keys.toString(keycode));
                switch (keycode) {
                    case Keys.LEFT:
                        cameraManager.moveCamera(-10, 0);
                        break;
                    case Keys.RIGHT:
                        cameraManager.moveCamera(10, 0);
                        break;
                    case Keys.UP:
                        cameraManager.moveCamera(0, 10);
                        break;
                    case Keys.DOWN:
                        cameraManager.moveCamera(0, -10);
                        break;
                    case Keys.COMMA:
                        cameraManager.adjustZoom(-0.1f);
                        break;
                    case Keys.PERIOD:
                        cameraManager.adjustZoom(0.1f);
                        break;
                }
                return true;
            }
        });
        inputMultiplexer.addProcessor(stage); // Make sure UI is still responsive
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void setupUI() {
        uimenuskin = new Skin(Gdx.files.internal(GameConstants.SKIN_UI),
                new TextureAtlas(GameConstants.TEXTURE_ATLAS_UI));
        Table layerControls = buildControls();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stack.add(layerControls);
    }

    private void initializeGameControllers() {
        this.enemyController = EnemyController.getInstance(this.level);
        this.towerController = TowerController.getInstance(this.level);

        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, new MouseController(towerController, enemyController, level));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private Table buildControls() { // move this to playscenemenu later on.
        Table layer = new Table();
        layer.setFillParent(true);
        layer.bottom();

        gunnerButton = new Button(uimenuskin, "gunnertower");
        layer.add(gunnerButton).padBottom(10);  // Add padding at the bottom if needed
        gunnerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onTowerClicked(DefenderType.GUNNER);
            }
        });

        sniperButton = new Button(uimenuskin, "snipertower");
        layer.add(sniperButton).padBottom(10);  // Add padding at the bottom if needed
        sniperButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onTowerClicked(DefenderType.SNIPER);
            }
        });

        bomberButton = new Button(uimenuskin, "bombertower");
        layer.add(bomberButton).padBottom(10);  // Add padding at the bottom if needed
        bomberButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onTowerClicked(DefenderType.BOMBER);
            }
        });

        speedUpgradeButton = new Button(uimenuskin, "bombertower");
        layer.add(speedUpgradeButton).padBottom(10).padLeft(70).size(50);  // Add padding at the bottom if needed
        speedUpgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                level.upgradeSpeedClicked();
            }
        });

        damageUpgradeButton = new Button(uimenuskin, "snipertower");
        layer.add(damageUpgradeButton).padBottom(10).size(50);  // Add padding at the bottom if needed
        damageUpgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                level.upgradeAttackClicked();
            }
        });

        rangeUpgradeButton = new Button(uimenuskin, "gunnertower");
        layer.add(rangeUpgradeButton).padBottom(10).size(50);  // Add padding at the bottom if needed
        rangeUpgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                level.upgradeRangeClicked();
            }
        });

        removeDefenderButton = new Button(uimenuskin, "bombertower");
        layer.add(removeDefenderButton).padBottom(10).padLeft(50).size(50);  // Add padding at the bottom if needed
        removeDefenderButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                towerController.sellSelectedDefender();
            }
        });
        layer.row();

        doubleSpeedButton = new Button(uimenuskin, "gunnertower");
        layer.add(doubleSpeedButton).padBottom(10).size(40);  // Add padding at the bottom if needed
        doubleSpeedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isToggledSpeed = !isToggledSpeed;
                if (isToggledSpeed) {
                    level.doubleSpeedClicked();
                } else {
                    level.normalSpeedClicked();
                }
            }
        });

        pauseButton = new Button(uimenuskin, "bombertower");
        layer.add(pauseButton).padBottom(10).size(40);  // Add padding at the bottom if needed
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isToggledPause = !isToggledPause;
                if (isToggledPause) {
                    level.pause();
                } else {
                    level.resume();
                }
            }
        });

        exitButton = new Button(uimenuskin, "snipertower");
        layer.add(exitButton).padBottom(10).size(40);  // Add padding at the bottom if needed
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                level.pause();
                game.setScreen(new MenuScene(game));
            }
        });
        layer.row();
        return layer;
    }

    private void onTowerClicked (DefenderType type) { // adding UI button for creating towers in playscene for now, will implement to maincontrolmenu soon. WIP
        towerController.setTowerSelected(type);
    }

    @Override
    public void render (float deltaTime) {
        Gdx.gl.glClearColor(0.392f, 0.584f, 0.929f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        level.update(deltaTime);
        spriteBatch.setProjectionMatrix(camera.combined);

        if (level != null) {
            spriteBatch.begin();
            level.getMap().render(spriteBatch);
            level.render(spriteBatch);
            spriteBatch.end();
        }

        renderInfo(spriteBatch);
        towerPlacementIndicator();
        towerUpgradeIndicator();
        towerRangeIndicator();

        // Render game world to screen
        stage.act(deltaTime);
        stage.draw();
        if (level.getUserHealth() <= 0){
            game.setScreen(new GameOverScene(game, level));
        }

    }
    private TextureRegion getTowerRegion(DefenderType type) {
        switch (type) {
            case GUNNER:
                return towerAtlas.findRegion("gunna1");
            case SNIPER:
                return towerAtlas.findRegion("snipa0");
            case BOMBER:
                return towerAtlas.findRegion("bomba0");
            default:
                return null;
        }
    }

    private void towerPlacementIndicator() {
        spriteBatch.begin();
        if (towerController.isTowerSelected()) {
            if (towerController.legalPlacement(Gdx.input.getX(), SCREEN_HEIGHT - Gdx.input.getY())) {
                spriteBatch.setColor(0, 1, 0, 0.5f);
            } else {
                spriteBatch.setColor(1, 0, 0, 0.5f);
            }
            TextureRegion region = getTowerRegion(towerController.getSelectedTowerType());
            spriteBatch.draw(region, Gdx.input.getX() - TOWER_SIZE/2, SCREEN_HEIGHT - Gdx.input.getY() - TOWER_SIZE/2, TOWER_SIZE, TOWER_SIZE);
        }
        spriteBatch.end();
    }

    private void towerUpgradeIndicator() {
        spriteBatch.begin();
        spriteBatch.enableBlending();
        for (BaseDefender tower : towerController.getDefenderList()) {
            if (tower == towerController.getSelectedDefenderUpgrade()) {
                spriteBatch.setColor(0, 1, 1, 1);
            } else {
                spriteBatch.setColor(1, 1, 1, 1);
            }
            tower.render(spriteBatch);
        }
        spriteBatch.end();
    }

    private void towerRangeIndicator() {
        if (towerController.getSelectedDefenderUpgrade() != null) {
            BaseDefender selectedTower = towerController.getSelectedDefenderUpgrade();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, 1);
            shapeRenderer.circle(selectedTower.center.x, selectedTower.center.y, selectedTower.getRange());
            shapeRenderer.end();
        }
    }

    private void renderInfo(SpriteBatch batch){
        spriteBatch.begin();

        String scoreText = "Score: " + level.getScore();
        String moneyText = "Money: " + level.getMoney();
        String waveText = "Wave: " + level.getCurrentWave();
        String enemiesText = "Enemies killed: " + level.getEnemiesKilled();
        String healthText = "Health: " + level.getUserHealth();

        float xCord = 10;
        float yCord = GameConstants.SCREEN_HEIGHT - 20;
        int padding = 20;

        GlyphLayout glyphScore = bitmapFont.draw(batch, scoreText, xCord, yCord);
        xCord += glyphScore.width + padding;

        GlyphLayout glyphMoney = bitmapFont.draw(batch, moneyText, xCord, yCord);
        xCord += glyphMoney.width + padding;

        GlyphLayout glyphHealth = bitmapFont.draw(batch, enemiesText, xCord, yCord);
        xCord += glyphHealth.width + padding;

        bitmapFont.draw(batch, healthText, xCord, yCord);

        xCord = GameConstants.SCREEN_WIDTH / 2;
        GlyphLayout glyphWave = new GlyphLayout();
        glyphWave.setText(bitmapFont, waveText);

        bitmapFont.draw(batch, waveText, xCord - glyphWave.width / 2, yCord);

        spriteBatch.end();
    }



    @Override
    public void resize (int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
            camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
            camera.update();
        }
    }

    @Override
    public void show() {
        //controlMenu = new MainControlMenu(level, towerController);
        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, new MouseController(towerController, enemyController, level));
        Gdx.input.setInputProcessor(inputMultiplexer);
        if(GameSettings.getMusic() == true) {
            MusicManager.play("gamemusic.ogg", true);
        }

        // temporary multiplexer to use both stage ui buttons and configurer mousecontroller bs so that i can place the towers. (temp temp temp!!!)
    }
    @Override
    public void hide () {
        MusicManager.stopCurrentMusic();
        spriteBatch.dispose();
        Gdx.input.setCatchBackKey(false);
        stage.dispose();
        uimenuskin.dispose();
    }

    @Override
    public void pause () {
        paused = true;
    }

    @Override
    public void resume () {
        super.resume();
    }

    public Object getLevel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLevel'");
    }
}