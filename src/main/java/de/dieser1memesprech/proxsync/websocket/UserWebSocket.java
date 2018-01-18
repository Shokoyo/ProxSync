package de.dieser1memesprech.proxsync.websocket;

import com.google.firebase.database.DataSnapshot;
import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync._9animescraper.util.AnimeUtils;
import de.dieser1memesprech.proxsync._9animescraper.util.HtmlUtils;
import de.dieser1memesprech.proxsync.database.Database;
import de.dieser1memesprech.proxsync.user.Room;
import de.dieser1memesprech.proxsync.user.RoomHandler;
import de.dieser1memesprech.proxsync.user.User;
import de.dieser1memesprech.proxsync.user.Video;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.json.*;
import javax.json.spi.JsonProvider;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.xml.bind.Element;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        if (r != null) {
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

        if("streamId".equals(jsonMessage.getString("action"))) {
            Database.updateStreamId(jsonMessage.getString("id"));
        }

        if ("changeName".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            String name = jsonMessage.getString("name");
            if (name != null && !name.equals("") && old != null) {
                old.changeName(session, name);
            }
        }

        if ("create".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if (old != null) {
                old.removeSession(session);
            }
            System.out.println(jsonMessage.getString("uid"));
            Room r = new Room(session, jsonMessage.getString("name"), jsonMessage.getString("uid"), jsonMessage.getBoolean("anonymous"));
            RoomHandler.getInstance().mapSession(session, r);
        }

        if ("finished".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.videoFinished();
            }
        }

        if ("autoNext".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.setAutoNext(jsonMessage.getBoolean("value"));
            }
        }

        if ("uid".equals(jsonMessage.getString("action"))) {
            System.out.println(jsonMessage.getString("value"));
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            r.getUserMap().get(session).setUid(jsonMessage.getString("value"));
        }

        if ("episodeLink".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.reset9anime();
                String name = jsonMessage.getString("name");
                String url = AnimeUtils.makeDirectLinkForNameAndEpisode(name, jsonMessage.getInt("episode"));
                r.addVideo(url);
            }
        }

        if ("search".equals(jsonMessage.getString("action"))) {
            System.out.println("Sending search request for keyword: " + jsonMessage.getString("keyword"));
            String s = jsonMessage.getString("keyword");
            if (s.toLowerCase().contains("zitat")) {
                s = "Youkoso Jitsuryoku Shijou Shugi no Kyoushitsu e";
            }
            List<AnimeSearchObject> animeSearchObjectList = AnimeUtils.search(s);
            JsonProvider provider = JsonProvider.provider();
            JsonArrayBuilder jsonArray = Json.createArrayBuilder();
            Object data = Database.getDataFromDatabase("users/" + RoomHandler.getInstance().getRoomBySession(session).getUserMap().get(session).getUid() + "/watchlist").getValue();
            Map<String, Object> dataMap = new LinkedHashMap<>();
            try {
                dataMap = (Map<String, Object>) data;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            for (AnimeSearchObject animeSearchObject : animeSearchObjectList) {
                jsonArray.add(Json.createObjectBuilder()
                        .add("title", animeSearchObject.getTitle())
                        .add("link", animeSearchObject.getLink())
                        .add("image", animeSearchObject.getPoster())
                        .add("lastEpisode", animeSearchObject.getLastEpisode())
                        .add("watchlist", -1)
                        .add("episodeCount", animeSearchObject.getEpisodeCount()));
            }
            javax.json.JsonArray array = jsonArray.build();
            JsonObject messageJson = provider.createObjectBuilder()
                    .add("action", "search-result").add("result", array).build();
            UserSessionHandler.getInstance().sendToSession(session, messageJson);
        }

        if ("changeRoomName".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if (RoomHandler.getInstance().checkId(jsonMessage.getString("name"))) {
                System.out.println("available");
                if (old != null) {
                    old.setId(jsonMessage.getString("name"));
                    JsonProvider provider = JsonProvider.provider();
                    JsonObject messageJson = provider.createObjectBuilder()
                            .add("action", "newRoomId")
                            .add("id", old.getId())
                            .build();
                    UserSessionHandler.getInstance().sendToRoom(messageJson, old);
                }
            }
        }

        if ("join".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if (old != null) {
                old.removeSession(session);
            }
            System.out.println(jsonMessage.getString("uid"));
            try {
                String id = jsonMessage.getString("id");
                Room r = RoomHandler.getInstance().getRoomById(id);
                if (r == null) {
                    JsonProvider provider = JsonProvider.provider();
                    JsonObject messageJson = provider.createObjectBuilder()
                            .add("action", "roomID")
                            .add("id", "-1")
                            .build();
                    UserSessionHandler.getInstance().sendToSession(session, messageJson);
                } else {
                    r.addSession(session, jsonMessage.getString("name"), jsonMessage.getString("uid"), jsonMessage.getBoolean("anonymous"));
                    RoomHandler.getInstance().mapSession(session, r);
                }
            } catch (NumberFormatException e) {

            }
        }

        if ("resync".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.pause(jsonMessage.getJsonNumber("current"), session, false);
            }
        }

        if ("video".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.reset9anime();
                r.addVideo(jsonMessage.getString("url"));
            }
        }

        if ("playNow".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.playNow(jsonMessage.getInt("episode"));
            }
        }

        if ("delete".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.delete(jsonMessage.getInt("episode"));
            }
        }

        if ("play".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.play();
            }
        }

        if ("stopped".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                boolean intended = jsonMessage.getBoolean("intended");
                r.pause(jsonMessage.getJsonNumber("current"), session, intended);
            }
        }

        if ("bufferedIndication".equals(jsonMessage.getString("action"))) {
            JsonNumber readyState = jsonMessage.getJsonNumber("readyState");
            if (readyState.intValue() == 4) {
                Room r = RoomHandler.getInstance().getRoomBySession(session);
                if (r != null) {
                    r.markReady(session, true);
                }
            }
        }

        if ("leave".equals(jsonMessage.getString("action"))) {
            Room old = RoomHandler.getInstance().getRoomBySession(session);
            if (old != null) {
                old.removeSession(session);
            }
        }

        if ("current".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                r.setCurrent(jsonMessage.getJsonNumber("current"));
            }
        }

        if ("addToWatchlist".equals(jsonMessage.getString("action"))) {
            Room r = RoomHandler.getInstance().getRoomBySession(session);
            if (r != null) {
                User user = r.getUserMap().get(session);
                if (!user.isAnonymous()) {
                    Video video;
                    if(r.getPlaylist().isEmpty()) {
                        video = r.getLastVideo();
                        if(video == null) {
                            return;
                        }
                    } else {
                        video = r.getPlaylist().peek();
                    }
                    int offset = 0;
                    if (jsonMessage.getBoolean("next")) {
                        offset = 1;
                    }
                    String status = "watching";
                    if (!jsonMessage.getBoolean("next") && video.getEpisode() <= 1) {
                        status = "planned";
                    }
                    String key = video.getKey();
                    int epNum = video.getEpisode() - 1 + offset;
                    if (("" + epNum).equals(video.getEpisodeCount())) {
                        status = "completed";
                    }
                    Database.addToWatchlist(key, epNum + "", status, user.getUid(), session);
                }
            }
        }

        reader.close();
    }

    private int getEpisodenumFromWatchlist(Map<String, Object> dataMap, String link) {
        int num = -1;
        String key = link.substring(link.lastIndexOf("/") + 1);
        key = key.replaceAll("\\.", "-");
        if (dataMap.size() > 0) {
            Map map = (Map) dataMap.get(key);
            if (map != null) {
                num = Integer.parseInt(map.get("episode").toString());
            }
        }
        return num;
    }
}
