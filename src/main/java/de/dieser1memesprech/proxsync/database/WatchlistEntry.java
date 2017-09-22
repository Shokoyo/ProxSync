package de.dieser1memesprech.proxsync.database;

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
    private String title;
    private String episodeCount;
    private long rating;
    private String key;
    private String status;

    public WatchlistEntry() {
    }

    public WatchlistEntry(String episode, String poster, String title, String episodeCount, long rating, String key, String status) {
        this.episode = episode;
        this.poster = poster;
        this.title = title;
        this.episodeCount = episodeCount;
        this.rating = rating;
        this.key = key;
        this.status = status;
    }

    public WatchlistEntry(String key, String episode, String poster, String title, String episodeCount, long rating) {
        this.episode = episode;
        this.rating = rating;
        this.poster = poster;
        this.title = title;
        this.episodeCount = episodeCount;
        this.key = key;
    }

    public String getEpisode() {
        return episode;
    }

    public String getPoster() {
        return poster;
    }

    public String getTitle() {
        return title;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public long getRating() {
        return rating;
    }

    public String getKey() {
        return key;
    }

    public String getStatus() {
        return status;
    }
}
