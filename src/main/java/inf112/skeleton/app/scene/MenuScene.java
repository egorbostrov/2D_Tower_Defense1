package inf112.skeleton.app.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import inf112.skeleton.app.TDGame;
import inf112.skeleton.app.level.Level;
import inf112.skeleton.app.util.GameAssets;
import inf112.skeleton.app.util.GameConstants;
import inf112.skeleton.app.util.MusicManager;

public class MenuScene extends AbstractGameScene {
    private Stage stage;
    private Skin uimenuskin;
    //BUTTONS
    private Button playButton;
    private Button exitButton;
    private Button optionsButton;
    private Image bgimg;

    public MenuScene(Game game) {
        super(game);
    }

    private void build() {
        uimenuskin = new Skin(Gdx.files.internal(GameConstants.SKIN_UI),
                new TextureAtlas(GameConstants.TEXTURE_ATLAS_UI));

        Table layerBackground = buildBg();
        Table layerControls = buildControls();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(GameConstants.UI_WIDTH, GameConstants.UI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);

    }

    private Table buildBg() {   // move this to menuscenemenu later on.
        Table layer = new Table();
        layer.setFillParent(true);
        // + Background
        bgimg = new Image(uimenuskin, "background");
        bgimg.setScaling(Scaling.stretch);
        bgimg.setFillParent(true);
        layer.add(bgimg).expand().fill();
        return layer;
    }

    private Table buildControls() { // move this to menuscenemenu later on.
        Table layer = new Table();
        // + Play Button
        playButton = new Button(uimenuskin, "play");
        layer.add(playButton);
        playButton.addListener(new ChangeListener() { // todo: lage general lambda-expression for listeners
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("play was pressed");
                onPlayClicked();
            }
        });
        layer.row();


        //+ Options Button
        optionsButton = new Button(uimenuskin, "options");
        layer.add(optionsButton);
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });
        layer.row();

        //+ exit button
        exitButton = new Button(uimenuskin, "exit");
        layer.add(exitButton);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onExitClicked();
            }
        });
        return layer;
    }

    private void onExitClicked() {
        System.exit(0);
    }

    private void onPlayClicked () {
        game.setScreen(new PlayScene(game));
    }

    private void onOptionsClicked () {
        game.setScreen(new OptionScene(game));

    }
    @Override
    public void render (float deltaTime) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override
    public void hide () {
        MusicManager.stopCurrentMusic();
        stage.dispose();
        uimenuskin.dispose();
    }
    @Override
    public void show () {
        MusicManager.play("bumer.ogg", true);
        stage = new Stage(new StretchViewport(GameConstants.UI_WIDTH, GameConstants.UI_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        build();
    }
    @Override public void pause () { }
}