package de.dieser1memesprech.proxsync.database;

import com.google.firebase.database.*;

import java.util.*;

public class Database {
    public static FirebaseDatabase database;

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
            ref.removeEventListener(listener);
            return listener.getData();
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
        Notification n = new Notification(key, title, latestEp, epCount);
        database.getReference("notifications/" + uid + "/" + key.replaceAll("\\.", "-")).setValue(n);
    }

    public static List<Notification> getNotifications(String uid) {
        List<Notification> res = new ArrayList<>();
        DataSnapshot data = getDataFromDatabase("notifications/" + uid);
        if (data != null && data.getChildrenCount() > 0) {
            for (DataSnapshot d : data.getChildren()) {
                res.add(d.getValue(Notification.class));
            }
        }
        return res;
    }

    public static Notification getNotification(String uid, String key) {
        DataSnapshot data = getDataFromDatabase("notifications/" + uid + "/" + key.replaceAll("\\.", "-"));
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

    public static void addToWatchlist(String key, String episode, String status, String uid) {
        Map<String, Object> dataMapWatchlist = new LinkedHashMap<String, Object>();
        dataMapWatchlist.put("episode", episode);
        dataMapWatchlist.put("rating", getWatchlistRating(uid, key));
        dataMapWatchlist.put("status", status);
        AnimeEntry entry = getAnimeEntryFromDatabase(key.replaceAll("\\.", "-"));
        dataMapWatchlist.put("title", entry.getTitle());
        dataMapWatchlist.put("episodeCount", entry.getEpisodeCount());
        dataMapWatchlist.put("poster", entry.getPoster());
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

    public static void removeNotification(String uid, String key) {
        database.getReference("notifications/" + uid + "/" + key.replaceAll("\\.", "-")).removeValue();
    }
}
