package com.mygdx.game;

import java.util.ArrayList;

/**
 * Created by Bladecla on 8/24/2016.
 */
public class Level extends ArrayList<Room> {

    //fields
   private Room startRoom;
   private Room currentRoom;
//constructors
    public Level(){

    }
    public Level (Room startRoom)
    {
        this.startRoom = startRoom;
        super.add(0, startRoom);
        setCurrentRoom(startRoom);
    }

    public Level (String startRoomTmx, char roomId)
    {
        this.startRoom = new Room(startRoomTmx, roomId);
        super.add(0, startRoom);
        setCurrentRoom(startRoom);
    }

    //properties

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;

    }

    public Room currentRoom()
    {
        return currentRoom;
    }



    //methods
    public Room addRoom(String startRoomTmx, char roomId){
        Room room = new Room(startRoomTmx, roomId);
        super.add(room);
        return room;
    }

    public Room findRoom(char id)
    {

        for (short i = 0; i < super.size(); i++)
        {
            if (super.get(i).getId() == id)
                return super.get(i);
        }
        return null;
    }

}

