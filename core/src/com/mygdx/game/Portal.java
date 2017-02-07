package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by Bladecla on 8/24/2016.
 */
public class Portal {
    //fields


    private String id, dest;



    private Room parentRoom;
    private LevelManager lm;
    private Vector2 spawnLoc;

    public Portal(String id, String dest, Room parentRoom, LevelManager lm)
    {
        this.id = id;
        this.dest = dest;
        this.parentRoom = parentRoom;
        this.lm = lm;


    }


    public String getId() {
        return id;
    }
    public Room getParentRoom() {
        return parentRoom;
    }
    public Vector2 getSpawnLoc() {
        if (spawnLoc != null)
        {return spawnLoc;}
        else return null;
    }


    public void setSpawnLoc(Vector2 spawnLoc)
    {
        this.spawnLoc = spawnLoc;
    }

    public void transport(Player player)
    {
        System.out.println("INSTANT TRANSMISSION!!");

        Room room;

        if (dest.length() == 2)
        {
            room = lm.currentLevel().findRoom(dest.charAt(0));
            if (room == null)
            {
                room = lm.currentLevel().addRoom("maps/" + lm.getLevels().indexOf(lm.currentLevel()) +"/" + dest.charAt(0) +".tmx", dest.charAt(0));
//
            }

            lm.setCurrentRoom(room);
            System.out.println("Level " + lm.getLevels().indexOf(lm.currentLevel()) + ", Room " + lm.currentRoom().getId() + ", " +lm.currentRoom().file());

            lm.setDestPortal(dest);


        }
        else if (dest.length() > 2)
        {

        }
    }



}
