package de.dieser1memesprech.proxsync.database;

import com.google.firebase.database.*;
import com.google.firebase.tasks.OnCompleteListener;
import com.google.firebase.tasks.Task;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync.websocket.UserSessionHandler;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.util.*;

public class Database {
    public static FirebaseDatabase database;

    public static void updateStreamId(String id) {
        database.getReference("config/stream-id").setValueAsync(id);
    }

    public static void initStreamIdListener() {
        database.getReference("config/stream-id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("value event received");
                Configuration.instance.setStreamServerId(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addAnimeinfoToDatabase(String key, String title, List<String> res) {
        Map<String, Object> dataMapEpisodeNames = new LinkedHashMap<String, Object>();
        for (int i = 0; i < res.size(); i++) {
            dataMapEpisodeNames.put(Integer.toString(i), res.get(i));
        }

        Map<String, Object> dataMapAnimeInfo = new LinkedHashMap<String, Object>();
        dataMapAnimeInfo.put("title", title);
        dataMapAnimeInfo.put("episodenames", dataMapEpisodeNames);

        updateData("anime/animeinfo/" + key.replaceAll("\\.", "-"), dataMapAnimeInfo);
    }

    public static void updateData(String path, Map<String, Object> data) {
        DatabaseReference ref = database.getReference(path);
        ref.updateChildren(data);
    }

    public static Watchlist getWatchlistObjectFromDatabase(String uid) {
        long t1 = System.currentTimeMillis();
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/watchlist");
        System.out.println("Watchlist get: " + (System.currentTimeMillis() - t1) + "ms");
        Watchlist res = new Watchlist();
        List<WatchlistEntry> list;
        for (DataSnapshot snapshot : data.getChildren()) {
            try {
                WatchlistEntry entry = snapshot.getValue(WatchlistEntry.class);
                String status = entry.getStatus();
                if (status.equals("watching")) {
                    list = res.getWatching();
                } else if (status.equals("completed")) {
                    list = res.getCompleted();
                } else {
                    list = res.getPlanned();
                }
                list.add(entry);
            } catch (DatabaseException e) {
                e.printStackTrace();
                System.out.println(snapshot.toString());
            }
        }
        return res;
    }

    public static void updateAnimeInfo(String key, String latestEpisode, String episodeCount, String poster) {
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("latestEpisode", latestEpisode);
        dataMap.put("episodeCount", episodeCount);
        dataMap.put("poster", poster);
        updateData("anime/animeinfo/" + key.replaceAll("\\.", "-"), dataMap);
    }

    public static void setAvatar(String uid, String url) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("avatar", url);
        updateData("users/" + uid, map);
    }

    public static String getBannerFromDatabase(String uid) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/banner");
        return data.getValue(String.class);
    }

    public static String getAvatarFromDatabase(String uid) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/avatar");
        String res = data.getValue(String.class);
        if(res == null) {
            res = "null";
        }
        return res;
    }

    public static List<String> getFavoriteKeys(String uid) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/favorites");
        List<String> res = new ArrayList<>();
        if(data == null) {
            return res;
        }
        try {
            if (data.getValue() != null) {
                for (Map.Entry<String, Object> d : ((Map<String, Object>) data.getValue()).entrySet()) {
                    String key = d.getKey();
                    if (key != null) {
                        res.add(key);
                    }
                }
            }
        } catch(ClassCastException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<WatchlistEntry> getFavorites(String uid) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/favorites");
        List<WatchlistEntry> res = new ArrayList<>();
        if(data == null) {
            return res;
        }
        try {
            for (Map.Entry<String, Object> d : ((Map<String, Object>) data.getValue()).entrySet()) {
                String key = d.getKey();
                System.out.println(key);
                WatchlistEntry entry = getWatchlistEntry(uid, key);
                if (entry != null) {
                    res.add(entry);
                }
            }
        } catch(ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void setBanner(String uid, String url) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("banner", url);
        updateData("users/" + uid, map);
    }

    public static AnimeEntry getAnimeEntryFromDatabase(String key) {
        DataSnapshot data = getDataFromDatabase("anime/animeinfo/" + key.replaceAll("\\.", "-"));
        AnimeEntry res = data.getValue(AnimeEntry.class);
        return res;
    }

    public static String getEpisodeTitleFromDatabase(String key, int episode) {
        DataSnapshot data = getDataFromDatabase("anime/animeinfo/" + key.replaceAll("\\.", "-") + "/episodenames/" + (episode - 1));
        if (data != null) {
            return data.getValue(String.class);
        }
        return null;
    }

    public static DataSnapshot getDataFromDatabase(String path) {
        try {
            // attach a value listener to a Firebase reference
            DatabaseReference ref = database.getReference(path);
            LoadedValueEventListener listener = new LoadedValueEventListener();
            ref.addListenerForSingleValueEvent(listener);
            DataSnapshot data = listener.getData();
            ref.removeEventListener(listener);
            return data;
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addToWatchlistImporter(String key, String episode, String status, String animeTitle,
                                              String poster, String episodeCount, String uid, String rating) {
        Map<String, Object> dataMapWatchlist = new LinkedHashMap<String, Object>();
        dataMapWatchlist.put("episode", episode);
        dataMapWatchlist.put("rating", rating);
        dataMapWatchlist.put("status", status);
        dataMapWatchlist.put("title", animeTitle);
        dataMapWatchlist.put("episodeCount", episodeCount);
        dataMapWatchlist.put("poster", poster);
        dataMapWatchlist.put("key", key);
        updateData("users/" + uid + "/watchlist/" + key.replaceAll("\\.", "-"), dataMapWatchlist);
        if (!"completed".equals(status)) {
            Map<String, Object> mapNew = new LinkedHashMap<>();
            mapNew.put(uid, episode);
            updateData("watching/" + key.replaceAll("\\.", "-"), mapNew);
        } else {
            database.getReference("watching/" + key.replaceAll("\\.", "-") + "/" + uid).removeValue();
        }
        Notification notification = getNotification(uid, key);
        if (notification != null && episode.equals(notification.getLatestEpisode())) {
            removeNotification(uid, key);
        }
    }

    public static WatchlistEntry getWatchlistEntry(String uid, String key) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/watchlist/" + key.replaceAll("\\.", "-"));
        return data.getValue(WatchlistEntry.class);
    }

    public static long getWatchlistRating(String uid, String key) {
        DataSnapshot snapshot = getDataFromDatabase("users/" + uid + "/watchlist/" + key.replaceAll("\\.", "-") + "/rating");
        try {
            return (long) snapshot.getValue();
        } catch (ClassCastException | NullPointerException e) {
            System.out.println("Watchlist entry missing or malformated. Returning 0");
            return 0;
        }
    }

    //Note that the key must be a valid 9anime key
    public static void addNotification(String key, String uid, String title, String latestEp, String epCount) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/notifications/" + key.replaceAll("\\.", "-") + "/hidden");
        if(!(data != null && data.exists() && data.getValue(Boolean.class))) {
            Notification n = new Notification(key, title, latestEp, epCount, false);
            database.getReference("users/" + uid + "/notifications/" + key.replaceAll("\\.", "-")).setValue(n);
        }
    }

    public static List<Notification> getNotifications(String uid) {
        List<Notification> res = new ArrayList<>();
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/notifications");
        if (data != null && data.getChildrenCount() > 0) {
            for (DataSnapshot d : data.getChildren()) {
                Notification n = d.getValue(Notification.class);
                if(!n.isHidden()) {
                    res.add(n);
                }
            }
        }
        return res;
    }

    public static void addAiringInfo(List<AiringEntry> l) {
        database.getReference("anime/airing").removeValue((databaseError, databaseReference) -> {
            for(AiringEntry e : l) {
                database.getReference("anime/airing/" + e.getId()).setValue(e);
            }
        });
    }

    public static AiringList getAiringList() {
        AiringList res = new AiringList();
        DataSnapshot data = getDataFromDatabase("anime/airing");
        if(data!= null) {
            for (DataSnapshot d : data.getChildren()) {
                AiringEntry entry = d.getValue(AiringEntry.class);
                String format = entry.getFormat();
                List<AiringEntry> list;
                if("TV".equals(format)) {
                    list = res.getTvList();
                } else if("MOVIE".equals(format)) {
                    list = res.getMovieList();
                } else if("TV_SHORT".equals(format)) {
                    list = res.getShortList();
                } else {
                    list = res.getOvaList();
                }
                list.add(entry);
            }
        }
        res.getOvaList().sort(Comparator.reverseOrder());
        res.getMovieList().sort(Comparator.reverseOrder());
        res.getTvList().sort(Comparator.reverseOrder());
        res.getShortList().sort(Comparator.reverseOrder());
        return res;
    }

    public static Notification getNotification(String uid, String key) {
        DataSnapshot data = getDataFromDatabase("users/" + uid + "/notifications/" + key.replaceAll("\\.", "-"));
        if (data != null) {
            return data.getValue(Notification.class);
        } else {
            return null;
        }
    }

    public static Map<String, String> getWatchingList(String key) {
        Map<String, String> res = new HashMap<>();
        DataSnapshot data = getDataFromDatabase("watching/" + key.replaceAll("\\.", "-"));
        if (data != null && data.getChildrenCount() > 0) {
            for (DataSnapshot d : data.getChildren()) {
                res.put(d.getKey(), (String) d.getValue());
            }
        }
        return res;
    }

    public static void addToWatchlist(String key, String episode, String status, String uid, Session session) {
        Map<String, Object> dataMapWatchlist = new LinkedHashMap<String, Object>();
        dataMapWatchlist.put("episode", episode);
        dataMapWatchlist.put("rating", getWatchlistRating(uid, key));
        dataMapWatchlist.put("status", status);
        AnimeEntry entry = getAnimeEntryFromDatabase(key.replaceAll("\\.", "-"));
        dataMapWatchlist.put("title", entry.getTitle());
        dataMapWatchlist.put("episodeCount", entry.getEpisodeCount());
        dataMapWatchlist.put("poster", entry.getPoster());
        dataMapWatchlist.put("key", key);
        DatabaseReference ref = database.getReference("users/" + uid + "/watchlist/" + key.replaceAll("\\.", "-"));
        ref.updateChildren(dataMapWatchlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                JsonProvider provider = JsonProvider.provider();
                JsonObject messageJson = provider.createObjectBuilder()
                        .add("action", "watchlist-oncomplete")
                        .add("anime", entry.title)
                        .build();
                UserSessionHandler.getInstance().sendToSession(session, messageJson);
            }
        });
        if (!"completed".equals(status)) {
            Map<String, Object> mapNew = new LinkedHashMap<>();
            mapNew.put(uid, episode);
            updateData("watching/" + key.replaceAll("\\.", "-"), mapNew);
        } else {
            database.getReference("watching/" + key.replaceAll("\\.", "-") + "/" + uid).removeValue();
        }
        Notification notification = getNotification(uid, key);
        if (notification != null && episode.equals(notification.getLatestEpisode())) {
            removeNotification(uid, key);
        }
    }

    public static void removeNotification(String uid, String key) {
        database.getReference("users/" + uid + "/notifications/" + key.replaceAll("\\.", "-")).removeValue();
    }
}
