package de.dieser1memesprech.proxsync.websocket;

import de.dieser1memesprech.proxsync.user.Room;
import de.dieser1memesprech.proxsync.user.RoomHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Jeremias on 06.08.2017.
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class UserWebSocket {
    @OnOpen
    public void open(Session session) {
        UserSessionHandler.getInstance().addSession(session);
    }

    @OnClose
    public void close(Session session) {
        Room r = RoomHandler.getInstance().getRoomBySession(session);
        if(r != null) {
            r.removeSession(session);
        }
        UserSessionHandler.getInstance().removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(UserWebSocket.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        JsonReader reader = Json.createReader(new StringReader(message));
        JsonObject jsonMessage = reader.readObject();

        if("changeName".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            String name = jsonMessage.getString("name");
            if(name != null && !name.equals("") && old != null) {
                old.changeName(session, name);
            }
        }

        if ("create".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if(old != null) {
                old.removeSession(session);
            }
            String s = jsonMessage.getString("roomName");
            if(s==null) {
                s = "";
            } else if (!s.equals("")) {
                if(!RoomHandler.getInstance().checkId(s)) {
                    System.out.println("ALARM");
                    JsonProvider provider = JsonProvider.provider();
                    JsonObject messageJson = provider.createObjectBuilder()
                            .add("action", "roomID")
                            .add("id", "-2")
                            .build();
                    UserSessionHandler.getInstance().sendToSession(session, messageJson);
                    return;
                }
            }
            Room r = new Room(session, jsonMessage.getString("name"),s);
            RoomHandler.getInstance().mapSession(session, r);
        }

        if ("join".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if(old != null) {
                old.removeSession(session);
            }
            try {
                String id = jsonMessage.getString("id");
                Room r = RoomHandler.getInstance().getRoomById(id);
                if(r==null) {
                    JsonProvider provider = JsonProvider.provider();
                    JsonObject messageJson = provider.createObjectBuilder()
                            .add("action", "roomID")
                            .add("id", "-1")
                            .build();
                    UserSessionHandler.getInstance().sendToSession(session, messageJson);
                } else {
                    r.addSession(session, jsonMessage.getString("name"));
                    RoomHandler.getInstance().mapSession(session, r);
                }
            } catch (NumberFormatException e) {

            }
        }

        if("resync".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if(r != null) {
                r.pause(jsonMessage.getJsonNumber("current"), session, false);
            }
        }

        if ("video".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if(r != null) {
                r.setVideo(jsonMessage.getString("url"));
            }
        }

        if ("play".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if(r != null) {
                r.play();
            }
        }

        if ("stopped".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if(r != null) {
                boolean intended = jsonMessage.getBoolean("intended");
                r.pause(jsonMessage.getJsonNumber("current"), session, intended);
            }
        }

        if ("bufferedIndication".equals(jsonMessage.getString("action"))) {
            JsonNumber readyState = jsonMessage.getJsonNumber("readyState");
            if(readyState.intValue() == 4) {
                Room r = RoomHandler.getInstance().getRoomBySession(session);
                if(r!=null) {
                    r.markReady(session, true);
                }
            }
        }

        if("leave".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if(old != null) {
                old.removeSession(session);
            }
        }

        if("current".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if(r != null) {
                r.setCurrent(jsonMessage.getJsonNumber("current"));
            }
        }

        reader.close();
    }
}
