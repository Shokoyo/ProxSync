package de.dieser1memesprech.proxsync.user;

import de.dieser1memesprech.proxsync.websocket.UserSessionHandler;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Room {
    private HashMap<Session, Boolean> readyStates = new HashMap<Session, Boolean>();
    private List<Session> sessions;
    private String video;
    private Session host;
    private int id;
    private JsonNumber currentTime = new JsonNumber() {
        public boolean isIntegral() {
            return false;
        }

        public int intValue() {
            return 0;
        }

        public int intValueExact() {
            return 0;
        }

        public long longValue() {
            return 0;
        }

        public long longValueExact() {
            return 0;
        }

        public BigInteger bigIntegerValue() {
            return null;
        }

        public BigInteger bigIntegerValueExact() {
            return null;
        }

        public double doubleValue() {
            return 0;
        }

        public BigDecimal bigDecimalValue() {
            return null;
        }

        public ValueType getValueType() {
            return null;
        }
    };

    public Room(Session host) {
        this.host = host;
        Random random = new Random();
        do {
            id = random.nextInt(999);
        } while(!RoomHandler.getInstance().checkId(id));
        sessions = new LinkedList<Session>();
        this.addSession(host);
        RoomHandler.getInstance().addRoom(this);
    }

    public boolean isHost(Session s) {
        return s == host;
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
