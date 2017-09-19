package de.dieser1memesprech.proxsync.database;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class WatchlistEntry {
    private String animeKey;
    private String episode;
    private String poster;
    private String animeTitle;
    private String episodeCount;

    public WatchlistEntry(String animeKey, String episode, String poster, String animeTitle, String episodeCount) {
        this.animeKey = animeKey;
        this.episode = episode;
        this.poster = poster;
        this.animeTitle = animeTitle;
        this.episodeCount = episodeCount;
    }

    public String getAnimeKey() {
        return animeKey;
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
}
