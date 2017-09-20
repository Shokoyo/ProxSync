package de.dieser1memesprech.proxsync.database;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class WatchlistEntry {
    private String episode;
    private String poster;
    private String animeTitle;
    private String episodeCount;
    private String key;

    public WatchlistEntry(String episode, String poster, String animeTitle, String episodeCount, String key) {
        this.episode = episode;
        this.poster = poster;
        this.animeTitle = animeTitle;
        this.episodeCount = episodeCount;
        this.key = key;
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

    public String getKey() {
        return key;
    }
}
