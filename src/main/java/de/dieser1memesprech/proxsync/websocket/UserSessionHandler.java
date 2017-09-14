package de.dieser1memesprech.proxsync.websocket;

import de.dieser1memesprech.proxsync.user.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
public class UserSessionHandler {
    private long lastProxerRequest = 0;
    private final Set<Session> sessions = new HashSet<Session>();
    private static UserSessionHandler instance;

    private UserSessionHandler() {}

    public static UserSessionHandler getInstance() {
        if(instance == null) {
            instance = new UserSessionHandler();
        }
        return instance;
    }

    public boolean proxRequest() {
        if(System.currentTimeMillis() < lastProxerRequest + 30000) {
            return false;
        } else {
            lastProxerRequest = System.currentTimeMillis();
            return true;
        }
    }

    public void addSession(javax.websocket.Session session) {
        sessions.add(session);
    }

    public void removeSession(javax.websocket.Session session) {
        sessions.remove(session);
    }

    public List<Session> getSessions() {
        return new ArrayList<Session>(sessions);
    }

    public void addUser(Session session) {
    }

    public void removeUser(int id) {
    }

    public void sendToRoom(JsonObject message, Room room) {
        for(Session s: room.getSessions()) {
            sendToSession(s,message);
        }
    }

    public synchronized void sendToSession(javax.websocket.Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
        }
    }
}
