package com.mygdx.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.game.Constants.PPM;

/**
 * Created by Bladecla on 8/25/2016.
 */
public class Player implements InputProcessor {

    private Sprite standLeft, standRight;
    private Animation previousAnimation, currentAnimation, walkLeft, walkRight;


    private int height, width;



    private Vector2 position;
    private Vector2 velocity;



    private TextureAtlas left, right, stand;
    private float timer = 0, walkSpeed;

    private BodyDef bdef = new BodyDef();
    private FixtureDef fdef = new FixtureDef();



    private Body body;



    private boolean dead = false;


    public Player(Vector2 location, World world)
    {
        this.position = location;
        velocity = new Vector2().setZero();
        walkSpeed = 3f;

        left = new TextureAtlas(Gdx.files.internal("left.atlas"));
        right = new TextureAtlas(Gdx.files.internal("right.atlas"));
        stand = new TextureAtlas(Gdx.files.internal("stand.atlas"));
        standLeft = stand.createSprite("stand1");
        standRight = stand.createSprite("stand");
        walkLeft = new Animation(1/12.5f, left.getRegions());
        walkRight = new Animation(1/12.5f, right.getRegions());
        currentAnimation = walkLeft;
        createBody(world);
    }

    //properties

    public Vector2 position() {
        return position;
    }

    public int getHeight() {
        height = (int)standLeft.getHeight();
        return height;
    }

    public int getWidth() {
        width = (int)standRight.getWidth();
        return width;
    }

    public Body getBody() {
        return body;
    }

    public boolean isDead() {
        return dead;
    }



    //methods

    public void draw(SpriteBatch batch) {
        if (velocity.isZero()) {
            if (previousAnimation == walkRight) {
                batch.draw(standRight, position.x, position.y, standRight.getWidth() / PPM, standRight.getHeight() / PPM);
            } else if (previousAnimation == walkLeft || previousAnimation == null) {
                batch.draw(standLeft, position.x, position.y, standLeft.getWidth() / PPM, standLeft.getHeight() / PPM);
            }

        } else if (!velocity.isZero()) {
            batch.draw(currentAnimation.getKeyFrame(timer, true), position.x, position.y, currentAnimation.getKeyFrame(timer).getRegionWidth() / PPM, currentAnimation.getKeyFrame(timer).getRegionHeight() / PPM);
        }
    }

    public void update() {

        if (!dead) {
            timer += Gdx.graphics.getDeltaTime();
            position.x = body.getPosition().x;
            position.y = body.getPosition().y;
        }
    }



    public void createBody(World world)
    {
        PolygonShape shape = new PolygonShape();

        float S = getWidth();
        float a = (float) ((S/(1+Math.sqrt(2)))/Math.sqrt(2));

        Vector2[] verts = new Vector2[8];
        verts[0] = new Vector2(0, a);
        verts[1] = new Vector2(a, 0);
        verts[2] =  new Vector2(S-a, 0);
        verts[3] = new Vector2(S, a);
        verts[4] = new Vector2(S, S-a);
        verts[5] = new Vector2(S-a, S);
        verts[6] = new Vector2(a, S);
        verts[7] = new Vector2(0, S-a);



        for (Vector2 points: verts)
        {
            points.scl(1/PPM/2);
            points.x += S/4/PPM;
        }



        shape.set(verts);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(position.x, position.y);

        fdef.shape = shape;
        fdef.density = 1.0f;

        body = world.createBody(bdef);
        body.setFixedRotation(true);
        body.createFixture(fdef).setUserData(this);



        shape.dispose();
        dead = false;
    }

    public void moveBody()
    {
        if (!dead)
        body.setLinearVelocity(velocity.x * PPM, velocity.y * PPM);
    }



    public void teleport( Vector2 spawnLoc)
    {
        dead = true;
        if (spawnLoc != null) {
            position.x = spawnLoc.x - getWidth()/2/PPM;
            position.y = spawnLoc.y;
        } else System.out.println("spawn location is null");

    }

    public void dispose()
    {
        left.dispose();
        right.dispose();
        stand.dispose();
    }
    @Override
    public boolean keyDown(int keycode) {

        if((keycode == Keys.LEFT || keycode == Keys.A) && !Gdx.input.isKeyPressed(Keys.RIGHT | Keys.D))
        {
            if (velocity.y != 0)
                velocity.x -= walkSpeed/Math.sqrt(2)/PPM;
            else if (velocity.y == 0)
            velocity.x -= walkSpeed/PPM;
            moveBody();
            currentAnimation = walkLeft;
//
        }
        else if ((keycode == Keys.RIGHT || keycode == Keys.D)&& !Gdx.input.isKeyPressed(Keys.LEFT | Keys.A))
        {
            if (velocity.y != 0)
                velocity.x += walkSpeed/Math.sqrt(2)/PPM;
            else if (velocity.y == 0)
                velocity.x += walkSpeed/PPM;

            moveBody();
            currentAnimation = walkRight;
//
        }

        if(keycode == Keys.ESCAPE)
        {
            Gdx.app.exit();
        }


        if((keycode == Keys.UP || keycode == Keys.W)&& !Gdx.input.isKeyPressed(Keys.DOWN | Keys.S))
        {

            if (velocity.x != 0)
                velocity.y += walkSpeed/Math.sqrt(2)/PPM;
            else if (velocity.x == 0)
                velocity.y += walkSpeed/PPM;

            moveBody();
//
        }
        else if ((keycode == Keys.DOWN || keycode == Keys.S)&& !Gdx.input.isKeyPressed(Keys.UP | Keys.W))
        {
            if (velocity.x != 0)
                velocity.y -= walkSpeed/Math.sqrt(2)/PPM;
            else if (velocity.x == 0)
                velocity.y -= walkSpeed/PPM;
            moveBody();
//
        }
        return true;

    }


    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Keys.LEFT || keycode == Keys.A)
        {
            velocity.x = 0;

            if (velocity.y > 0)
            {
                velocity.y = walkSpeed/PPM;
            }
            else if (velocity.y < 0) {
                velocity.y = -walkSpeed / PPM;
            }
            moveBody();
            previousAnimation = currentAnimation;

        }

        else if (keycode == Keys.RIGHT || keycode == Keys.D)
        {

            velocity.x = 0;

            if (velocity.y > 0)
            {
                velocity.y = walkSpeed/PPM;
            }
            else if (velocity.y < 0) {
                velocity.y = -walkSpeed / PPM;
            }
            moveBody();
            previousAnimation = currentAnimation;

        }
        if(keycode == Keys.UP || keycode == Keys.W)
        {
            velocity.y = 0;

            if (velocity.x > 0)
        {
            velocity.x = walkSpeed/PPM;
        }
        else if (velocity.x < 0) {
                velocity.x = -walkSpeed / PPM;
            }
            moveBody();
        }

        else if (keycode == Keys.DOWN || keycode == Keys.S)
        {
            velocity.y = 0;

            if (velocity.x > 0)
            {
                velocity.x = walkSpeed/PPM;
            }
            else if (velocity.x < 0) {
                velocity.x = -walkSpeed / PPM;
            }
            moveBody();
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (screenY > position.y) {
            velocity.y = walkSpeed / PPM;
            moveBody();
        } else if (screenY < position.y) {
            velocity.y = -walkSpeed / PPM;
            moveBody();
        }

        if (screenX > position.x) {
            velocity.x = walkSpeed / PPM;
            currentAnimation = walkRight;
            moveBody();
        } else if (screenX < position.x)
        {
            velocity.x = -walkSpeed / PPM;
            currentAnimation = walkLeft;
            moveBody();
        }


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        velocity.setZero();
        moveBody();
        previousAnimation = currentAnimation;

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
