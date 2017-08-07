package de.dieser1memesprech.proxsync.user;

import de.dieser1memesprech.proxsync.websocket.UserSessionHandler;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.util.*;

public class Room {
    private HashMap<Session, Boolean> readyStates = new HashMap<Session, Boolean>();
    private List<Session> sessions;
    private String video;
    private int id;

    public Room() {
        Random random = new Random();
        do {
            id = random.nextInt(999);
        } while(!RoomHandler.getInstance().checkId(id));
        sessions = new LinkedList<Session>();
        RoomHandler.getInstance().addRoom(this);
    }

    public List<Session> getSessions() {
        return new LinkedList<Session>(sessions);
    }

    public void addSession(Session session) {
        readyStates.put(session, false);
        sessions.add(session);
    }

    public void removeSession(Session session) {
        readyStates.remove(session);
        sessions.remove(session);
    }

    public void setVideo(String url) {
        video = url;
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "video")
                .add("url", url)
                .build();
        UserSessionHandler.getInstance().sendToRoom(messageJson, this);
    }

    public void markReady(Session s, boolean status) {
        readyStates.put(s,status);
    }

    public void startSyncing() {
        boolean flag = true;
        for(Session s: sessions) {
            if(!readyStates.get(s)) {
                flag = false;
            }
        }
        if(flag) {
            //start playing
            JsonProvider provider = JsonProvider.provider();
            JsonObject messageJson = provider.createObjectBuilder()
                    .add("action", "play")
                    .build();
            UserSessionHandler.getInstance().sendToRoom(messageJson, this);
        } else {
            //check buffered status
            sendBufferedRequests();
        }
    }

    private void sendBufferedRequests() {
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "bufferedRequest")
                .build();
        UserSessionHandler.getInstance().sendToRoom(messageJson, this);
    }

    public int getId() {
        return id;
    }
}
