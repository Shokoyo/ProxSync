package de.dieser1memesprech.proxsync.websocket;

import de.dieser1memesprech.proxsync.user.User;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class UserSessionHandler {
    private final Set<Session> sessions = new HashSet<Session>();
    private final Set<User> users = new HashSet<User>();

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List<User> getUsers() {
        return new ArrayList<User>(users);
    }

    public void addUser(User user) {
    }

    public void removeUser(int id) {
    }

    private User getUserById(int id) {
        return null;
    }

    private JsonObject createAddMessage(User user) {
        return null;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
    }

    private void sendToSession(Session session, JsonObject message) {
    }
}
