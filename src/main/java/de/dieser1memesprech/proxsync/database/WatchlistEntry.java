package de.dieser1memesprech.proxsync.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.model.FirebaseResponse;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class WatchlistEntry {
    private String episode;
    private String poster;
    private String animeTitle;
    private String episodeCount;
    private int rating;
    private String animeKey;

    public WatchlistEntry(String animeKey, String episode, String poster, String animeTitle, String episodeCount, int rating) {
        this.episode = episode;
        this.rating = rating;
        this.poster = poster;
        this.animeTitle = animeTitle;
        this.episodeCount = episodeCount;
        this.animeKey = animeKey;
    }

    public WatchlistEntry(String uid, JsonObject object) {
        this.episode = object.get("episode").getAsString();
        try {
            this.rating = object.get("rating").getAsInt();
        } catch(ClassCastException | IllegalStateException e) {
            this.rating = Integer.parseInt(object.get("rating").getAsString());
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("rating", rating);
            try {
                FirebaseResponse response = Configuration.instance.getFirebase().patch("users/" + uid + "/watchlist/" + object.get("key"), map);
            } catch(FirebaseException | UnsupportedEncodingException | JacksonUtilityException ex) {
                e.printStackTrace();
            }
        }
        this.poster = object.get("poster").getAsString();
        this.animeTitle = object.get("title").getAsString();
        this.episodeCount = object.get("episodeCount").getAsString();
        this.animeKey = object.get("key").getAsString();
    }

    public int getRating() {
        return rating;
    }

    public String getEpisode() {
        return episode;
    }

    public String getPoster() {
        return poster;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public String getAnimeKey() {
        return animeKey;
    }
}
