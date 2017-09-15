package de.dieser1memesprech.proxsync.user;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RoomHandler {

    private Map<Session, Room> sessionMapper = new HashMap<Session, Room>();
    private List<Room> rooms;
    private static RoomHandler instance = null;

    public void mapSession(Session s, Room r) {
        sessionMapper.put(s,r);
    }

    public Room getRoomBySession(Session s) {
        return sessionMapper.get(s);
    }

    private RoomHandler() {
        rooms = new LinkedList<Room>();
    }

    public static RoomHandler getInstance() {
        if(instance == null) {
            instance = new RoomHandler();
        }
        return instance;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getRoomById(String id) {
        for(Room r: rooms) {
            if(r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }

    public boolean checkId(String id) {
        for(Room r: rooms) {
            if(r.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }
}
