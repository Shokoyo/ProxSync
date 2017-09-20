package de.dieser1memesprech.proxsync.database;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import net.thegreshams.firebase4j.service.Firebase;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static void addAnimeinfoToDatabase(String key, String title, List<String> res) {
        try {
            Map<String, Object> dataMapEpisodeNames = new LinkedHashMap<String, Object>();
            for (int i = 0; i < res.size(); i++) {
                dataMapEpisodeNames.put(Integer.toString(i), res.get(i));
            }

            Map<String, Object> dataMapAnimeInfo = new LinkedHashMap<String, Object>();
            dataMapAnimeInfo.put("title", title);
            dataMapAnimeInfo.put("episodenames", dataMapEpisodeNames);

            FirebaseResponse response = Configuration.instance.getFirebase().put("anime/animeinfo/" + key.replaceAll("\\.", "-"), dataMapAnimeInfo);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        }
    }

    public static Watchlist getWatchlistObjectFromDatabase(String uid) {
        long t1 = System.currentTimeMillis();
        FirebaseResponse dataResponse = Database.getWatchlist(uid);
        System.out.println("Watchlist get: " + (System.currentTimeMillis() - t1) + "ms");
        JsonElement json = new JsonParser().parse(dataResponse.getRawBody());
        Watchlist res = new Watchlist();
        if (json.isJsonObject()) {
            t1 = System.currentTimeMillis();
            for (Map.Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
                try {
                    String animeId = e.getKey();
                    JsonObject objectEntry = e.getValue().getAsJsonObject();
                    String episode = objectEntry.get("episode").getAsString();
                    String status = objectEntry.get("status").getAsString();
                    int rating = objectEntry.get("rating").getAsInt();
                    String animeTitle = objectEntry.get("title").getAsString();
                    String episodeCount = objectEntry.get("episodeCount").getAsString();
                    String poster = objectEntry.get("poster").getAsString();
                    String key = "";
                    try {
                        key = objectEntry.get("key").getAsString();
                    } catch(NullPointerException ex) {
                        System.out.println("no key for " + animeTitle);
                    }
                    List<WatchlistEntry> list;
                    if (status.equals("watching")) {
                        list = res.getWatching();
                    } else if (status.equals("completed")) {
                        list = res.getCompleted();
                    } else {
                        list = res.getPlanned();
                    }
                    list.add(new WatchlistEntry(episode, poster, animeTitle, episodeCount, key));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    System.out.println("Malformed anime or watchlist entry");
                    System.out.println("Watchlist entry: " + dataResponse.getRawBody());
                }
            }
        }
        System.out.println("Object generation: " + (System.currentTimeMillis() - t1) + "ms");
        return res;
    }

    public static void updateAnimeInfo(String key, String latestEpisode, String episodeCount, String poster) {
        try {
            JsonObject animeObject = getAnimeObjectFromDatabase(key);
            animeObject.addProperty("latestEpisode", latestEpisode);
            animeObject.addProperty("episodeCount", episodeCount);
            animeObject.addProperty("poster", poster);
            Type mapType = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();
            Map<String, Object> animeMapObject = new Gson().fromJson(animeObject, mapType);
            FirebaseResponse response = Configuration.instance.getFirebase().put("anime/animeinfo/" + key.replaceAll("\\.", "-"), animeMapObject);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        }
    }

    public static FirebaseResponse getAnimeFromDatabase(String key) {
        FirebaseResponse response = null;
        try {
            response = Configuration.instance.getFirebase().get("anime/animeinfo/" + key.replaceAll("\\.", "-"));
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void setAvatar(String uid, String url) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("avatar", url);
        try {
            FirebaseResponse response = Configuration.instance.getFirebase().patch("users/" + uid, map);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(JacksonUtilityException e) {
            e.printStackTrace();
        }
    }

    public static String getBannerFromDatabase(String uid) {
        FirebaseResponse response = null;
        try {
            response = Configuration.instance.getFirebase().get("users/" + uid + "/banner");
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(response != null && !response.getRawBody().equals("null")) {
            return new JsonParser().parse(response.getRawBody()).getAsString();
        } else {
            return "null";
        }
    }

    public static String getAvatarFromDatabase(String uid) {
        FirebaseResponse response = null;
        try {
            response = Configuration.instance.getFirebase().get("users/" + uid + "/avatar");
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(response != null && !response.getRawBody().equals("null")) {
            return new JsonParser().parse(response.getRawBody()).getAsString();
        } else {
            return "null";
        }
    }

    public static void setBanner(String uid, String url) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("banner", url);
        try {
            FirebaseResponse response = Configuration.instance.getFirebase().patch("users/" + uid, map);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(JacksonUtilityException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getAnimeObjectFromDatabase(String key) {
        FirebaseResponse response = getAnimeFromDatabase(key);
        JsonElement animeJson = new JsonParser().parse(response.getRawBody());
        if(!animeJson.isJsonObject()) {
            System.out.println("Malformed anime object: " + response.getRawBody());
            return new JsonObject();
        } else {
            return animeJson.getAsJsonObject();
        }
    }

    public static FirebaseResponse getEpisodeTitleFromDatabase(String key, int episode) {
        FirebaseResponse response = null;
        try {
            response = Configuration.instance.getFirebase().get("anime/animeinfo/" + key.replaceAll("\\.", "-") + "/episodenames/" + (episode - 1));
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void addToWatchlistImporter(String key, String episode, String status, String animeTitle,
                                              String poster, String episodeCount, String uid, String rating) {
        try {
            Map<String, Object> dataMapWatchlist = new LinkedHashMap<String, Object>();
            dataMapWatchlist.put("episode", episode);
            dataMapWatchlist.put("rating", rating);
            dataMapWatchlist.put("status", status);
            dataMapWatchlist.put("title", animeTitle);
            dataMapWatchlist.put("episodeCount", episodeCount);
            dataMapWatchlist.put("poster", poster);
            dataMapWatchlist.put("key", key);
            FirebaseResponse response = Configuration.instance.getFirebase().put("users/" + uid + "/watchlist/" + key.replaceAll("\\.", "-"), dataMapWatchlist);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        }
    }

    public static void addToWatchlist(String key, String episode, String status, String uid) {
        try {
            Map<String, Object> dataMapWatchlist = new LinkedHashMap<String, Object>();
            dataMapWatchlist.put("episode", episode);
            dataMapWatchlist.put("rating", "0");
            dataMapWatchlist.put("status", status);
            JsonObject animeObject = Database.getAnimeObjectFromDatabase(key);
            dataMapWatchlist.put("title", animeObject.get("title").getAsString());
            dataMapWatchlist.put("episodeCount", animeObject.get("episodeCount").getAsString());
            dataMapWatchlist.put("poster", animeObject.get("poster").getAsString());
            dataMapWatchlist.put("key", key);
            FirebaseResponse response = Configuration.instance.getFirebase().put("users/" + uid + "/watchlist/" + key.replaceAll("\\.", "-"), dataMapWatchlist);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        }
    }


    public static FirebaseResponse getWatchlist(String uid) {
        FirebaseResponse response = null;
        try {
            Firebase firebase = Configuration.instance.getFirebase();
            response = firebase.get("users/" + uid + "/watchlist");
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }


}
