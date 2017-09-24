package de.dieser1memesprech.proxsync.database;

/**
 * Created by Jeremias on 22.09.2017.
 */
public class Notification {
    private String key;
    private String title;
    private String latestEpisode;
    private String episodeCount;
    private boolean hidden;

    public Notification() {}

    public Notification(String key, String title, String latestEpisode, String episodeCount, boolean hidden) {
        this.key = key;
        this.title = title;
        this.latestEpisode = latestEpisode;
        this.episodeCount = episodeCount;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getLatestEpisode() {
        return latestEpisode;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public boolean isHidden() {
        return hidden;
    }
}
