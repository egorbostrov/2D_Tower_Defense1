package inf112.skeleton.app.entity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.enums.BulletType;
import inf112.skeleton.app.tower.BaseDefender;
import inf112.skeleton.app.tower.BomberDefender;
import inf112.skeleton.app.tower.GunnerDefender;
import inf112.skeleton.app.util.GameAssets;
import inf112.skeleton.app.util.GameConstants;
import inf112.skeleton.app.util.MusicManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BulletTest {

    private static HeadlessApplication application;

    @BeforeAll
    public static void setupBeforeAll() {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        application = new HeadlessApplication(new ApplicationAdapter() {}, config);
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
        when(Gdx.gl.glGenTexture()).thenReturn(1);
        GameAssets.instance.init();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testBulletHit() {
        List<Enemy> enemyList = new ArrayList<>();
        Enemy enemy = new Enemy('R', 110, 110, 50, 50, 1, new LinkedList<>(), 50, 0, 0, null, false);
        enemyList.add(enemy);
        BaseDefender defender = new GunnerDefender(100, 100, enemyList);

        defender.update(0);
        defender.projectileFire();
        assertFalse(defender.getBullets().isEmpty(), "Should be a bullet now");
        defender.update(1);


        assertTrue(defender.getBullets().isEmpty(), "Bullet should have been removed as it hit a zombie");
    }

    @AfterAll
    public static void tearDown() {
        if(application != null) {
            application.exit();
            application = null;
        }
        Gdx.gl = null;
        Gdx.gl20 = null;
    }
}
