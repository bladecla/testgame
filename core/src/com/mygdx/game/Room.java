package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMap;

import java.util.ArrayList;

/**
 * Created by Bladecla on 8/24/2016.
 */
public class Room
{


    //fields
    private char id;
    private String fileName;




    private ArrayList<Portal> portals;

    //constructors

    public Room (String fileName, char id){
        this.fileName = fileName;
        this.id = id;
        this.portals = new ArrayList<Portal>();
    }

    //properties
    public char getId() {
        return id;
    }

    public String file()
    {
        return fileName;
    }

    public ArrayList<Portal> getPortals() {
        return portals;
    }

    public void addPortal(Portal portal)
    {
        portals.add(portal);

    }

    public Portal findPortal(String id)
    {
        System.out.println("Portals: " + portals.size());
        for (short i = 0; i < portals.size(); i++)
        {
            if (portals.get(i).getId().equals(id))
                return portals.get(i);
        }
        return null;
    }
}
