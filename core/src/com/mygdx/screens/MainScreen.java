package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.*;

import static com.mygdx.game.Constants.PPM;

/**
 * Created by Bladecla on 8/20/2016.
 */



public class MainScreen implements Screen {

    private World world;
    private SomeGame game;

    private OrthographicCamera camera;
    private FitViewport port;
    private final float V_HEIGHT = 768 , V_WIDTH = 1366, SCALE = .6f;
    private OrthogonalTiledMapRenderer renderer;

    private LevelManager lm;
    private CollisionManager cm;

    private Player p1;

    private Box2DDebugRenderer b2dr;


    public MainScreen(SomeGame game)

    {
         this.game = game;

        camera = new OrthographicCamera();
        port = new FitViewport((V_WIDTH*SCALE) /PPM , (V_HEIGHT*SCALE) /PPM , camera);

        world = new World(new Vector2(0,0), true);
        p1 = new Player(new Vector2(5,3), world);
        cm = new CollisionManager();
        world.setContactListener(cm);
        lm = new LevelManager(world);
        lm.addLevel("maps/1/a.tmx", 'a');
        lm.setCurrentLevel(1);
        lm.parseObjects();

        System.out.println("Room " + lm.currentRoom().getId() + " Filename: " + lm.currentRoom().file());

        renderer = new OrthogonalTiledMapRenderer(lm.currentMap(), 1/PPM, game.batch);
        lm.setRenderer(renderer);
        camera.position.set(p1.position().x + p1.getWidth()/2/PPM ,p1.position().y + p1.getHeight()/2/PPM, 0);
        Gdx.input.setInputProcessor(p1);


        b2dr = new Box2DDebugRenderer();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (lm.deadBodies().size > 0 || p1.isDead())
        {
            if (lm.deadBodies().size > 0) {

//                System.out.println("Bodies: " + lm.deadBodies().size);
                for (Body body : lm.deadBodies()) {
                    world.destroyBody(body);
                }
//            System.out.println("Bodies: " + lm.deadBodies().size);
                lm.deadBodies().clear();
//                System.out.println("Bodies: " + lm.deadBodies().size);
                System.out.println("Room " + lm.currentRoom().getId() + ", Portals: " + lm.currentRoom().getPortals().size());
                lm.parseObjects();
                System.out.println("Room " + lm.currentRoom().getId() + ", Portals: " + lm.currentRoom().getPortals().size());
//                System.out.println("next portal is: " + lm.currentRoom().findPortal(lm.destPortal()));
                if (lm.currentRoom().findPortal(lm.destPortal()).getSpawnLoc() != null) {
                    p1.teleport(lm.currentRoom().findPortal(lm.destPortal()).getSpawnLoc());
                }


                for (Room rooms : lm.currentLevel())
                {
                    System.out.println("Portals in room " + rooms.getId() + ": ");

                    for (Portal portal : rooms.getPortals()) {
                        System.out.println(portal.getId());
                    }
                }
            }
            if (p1.isDead())
            {
                world.destroyBody(p1.getBody());

                p1.createBody(world);

            }
        }

        else world.step(1/60f, 6, 2);

        camera.update();
        p1.update();

        camera.position.y = p1.position().y + p1.getHeight()/2/PPM;
        camera.position.x = p1.position().x + p1.getWidth()/2/PPM;
        renderer.setView(camera);

        game.batch.setProjectionMatrix(camera.combined);
        renderer.render();
        game.batch.begin();

        p1.draw(game.batch);
        game.batch.end();


//        b2dr.render(world, camera.combined);
    }

    @Override
    public void resize(int width, int height) {
    port.update(width, height);
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
    lm.currentMap().dispose();
        renderer.dispose();
        p1.dispose();
        world.dispose();
        b2dr.dispose();
    }
}
