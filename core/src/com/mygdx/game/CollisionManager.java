package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Bladecla on 8/30/2016.
 */
public class CollisionManager implements ContactListener {

    @Override
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if (checkClass(fa, fb, Player.class, Portal.class))
        {

            if (fa.getUserData() instanceof Portal)
            {
                ((Portal) fa.getUserData()).transport((Player) fb.getUserData());
            }
            else if (fb.getUserData() instanceof Portal)
            {
                ((Portal) fb.getUserData()).transport((Player) fa.getUserData());
            }
        }

    }

    @Override
    public void endContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private boolean checkUserData(Fixture fa, Fixture fb, Object data1, Object data2){

        if ( fa.getUserData() != null && fb.getUserData() != null &&
                fa.getUserData().equals(data1) && fb.getUserData().equals(data2))
        {
            return true;
        } else if (fa.getUserData() != null && fb.getUserData() != null &&
                fb.getUserData().equals(data1) && fa.getUserData().equals(data2))
        {
            return true;
        }
        else return false;
    }

    private boolean checkClass(Fixture fa, Fixture fb, Class a, Class b){
        if ( fa.getUserData() != null && fb.getUserData() != null &&
                fa.getUserData().getClass().isAssignableFrom(a) && fb.getUserData().getClass().isAssignableFrom(b) )
        {
            return true;

        } else if (fa.getUserData() != null && fb.getUserData() != null &&
                fb.getUserData().getClass().isAssignableFrom(a) && fa.getUserData().getClass().isAssignableFrom(b))
        {
            return true;
        }
        else return false;
    }
}
