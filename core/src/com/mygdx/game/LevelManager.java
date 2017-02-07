package com.mygdx.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.mygdx.game.Constants.PPM;

/**
 * Created by Bladecla on 8/24/2016.
 */
public class LevelManager {



    //fields
    private ArrayList<Level> levels;
    private Array<Body> bodies = new Array<Body>(), killList = new Array<Body>();
    private World world;
    private OrthogonalTiledMapRenderer renderer;
    private TmxMapLoader loader;
    private Level overworld;



    private String destPortal;


    private Level currentLevel;


    //constructors

    public LevelManager(World world){
        this.levels =  new ArrayList<Level>();
        this.loader = new TmxMapLoader();
        levels.add(0, new Level());
        this.overworld = levels.get(0);
        this.currentLevel = overworld;
        this.world = world;

    }

    //properties
    public void setRenderer(OrthogonalTiledMapRenderer renderer)
    {
        this.renderer = renderer;
    }
    public ArrayList<Level> getLevels() {
        return levels;
    }

    public Level currentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;

    }

    public void setCurrentLevel(int index){
        this.currentLevel = levels.get(index);
    }

    public Room currentRoom(){
        return this.currentLevel.currentRoom();
    }

    public void setCurrentRoom(Room room){
        currentLevel().setCurrentRoom(room);
        killBodies();
        renderer.setMap(loader.load(room.file()));

//        parseObjects();

    }

    public String destPortal() {
        return destPortal;
    }

    public void setDestPortal(String destPortal) {
        this.destPortal = destPortal;
    }

    public Array<Body> deadBodies()
    {
        return killList;
    }

    private void killBodies() {
        killList.addAll(bodies);
        bodies.clear();
    }

    public TiledMap currentMap() {
       return loader.load(this.currentRoom().file());
    }

    //methods
    public void addLevel(String startRoomTmx, char roomId){
        levels.add(new Level(startRoomTmx, roomId));
    }

    public void parseObjects (){
        float[] verts;
        Body body;
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();



        //walls
        for (MapObject object : currentMap().getLayers().get("wall").getObjects())
        {


            if (object instanceof RectangleMapObject)
            {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                shape.setAsBox(rect.width/2/PPM , rect.height/2/PPM );

                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX()+rect.getWidth()/2)/PPM, (rect.getY()+rect.getHeight()/2)/PPM);
                fdef.shape = shape;

                body = world.createBody(bdef);
                body.createFixture(fdef);
                bodies.add(body);
            }
            else if (object instanceof PolygonMapObject)
            {
                Polygon poly = ((PolygonMapObject) object).getPolygon();
                verts = new float[poly.getTransformedVertices().length];
                for (int i = 0; i < poly.getTransformedVertices().length; i++)
                {
                    verts[i] = poly.getTransformedVertices()[i]/PPM;
                }
                shape.set(verts);
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.setZero();
                fdef.shape = shape;

                body = world.createBody(bdef);
                body.createFixture(fdef);
                bodies.add(body);

            }
            else if (object instanceof PolylineMapObject)
            {
                Polyline line = ((PolylineMapObject) object).getPolyline();
                verts = line.getTransformedVertices();
                Vector2[] worldVertices = new Vector2[verts.length/2];
                for(int i = 0; i < worldVertices.length; i++)
                {
                    worldVertices[i] = new Vector2(verts[i * 2]/PPM, verts[i * 2 + 1]/PPM);
                }

                ChainShape cs = new ChainShape();
                cs.createChain(worldVertices);

                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.setZero();
                fdef.shape = cs;

                body = world.createBody(bdef);
                body.createFixture(fdef);
                bodies.add(body);
                cs.dispose();
            }

            //portals


        }
        for (MapObject object : currentMap().getLayers().get("portal").getObjects())
        {
            Portal portal = new Portal(object.getName(), (String) object.getProperties().get("dest"), currentRoom(), this);

            if(currentRoom().getPortals().size() < currentMap().getLayers().get("portal").getObjects().getCount())
            {
                System.out.println("map count: " + currentMap().getLayers().get("portal").getObjects().getCount() );
            portal.getParentRoom().addPortal(portal);
                System.out.println("added " + portal.getId());
                System.out.println("now there are " + currentRoom().getPortals().size());
            }
//            System.out.println("Portals: " + currentRoom().getPortals().size());

            if (object instanceof RectangleMapObject)
            {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
                shape.setAsBox(rect.width/2/PPM, rect.height/2/PPM);
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX()+rect.getWidth()/2)/PPM, (rect.getY()+rect.getHeight()/2)/PPM);
                fdef.isSensor = true;
                fdef.shape = shape;
                body = world.createBody(bdef);
                body.createFixture(fdef).setUserData(portal);
                bodies.add(body);
            }
            else if (object instanceof PolylineMapObject)

            //must draw polylines with spawn location as last point

            {
                Polyline line = ((PolylineMapObject) object).getPolyline();
                verts = line.getTransformedVertices();
                LinkedList<Vector2> worldVertices = new LinkedList<Vector2>();
                for(int i = 0; i < verts.length/2; i++)
                {
                    worldVertices.add(i, new Vector2(verts[i * 2]/PPM, verts[i * 2 + 1]/PPM));
                }
//                System.out.println("size: " + worldVertices.size());
                portal.setSpawnLoc(worldVertices.getLast());
                worldVertices.removeLast();

                shape.set(worldVertices.toArray(new Vector2[worldVertices.size()-1]));
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.setZero();
                fdef.shape = shape;
                fdef.isSensor = true;

                body = world.createBody(bdef);
                body.createFixture(fdef).setUserData(portal);
                bodies.add(body);

            }


        }
        shape.dispose();
    }

}
