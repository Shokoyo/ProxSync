package de.dieser1memesprech.proxsync.database;

import java.util.List;

/**
 * Created by Jeremias on 22.09.2017.
 */
public class AnimeEntry {
    String episodeCount;
    List<String> episodenames;
    String latestEpisode;
    String poster;
    String title;

    public AnimeEntry() {}

    public AnimeEntry(String episodeCount, List<String> episodeTitles, String latestEpisode, String poster, String title) {
        this.episodeCount = episodeCount;
        this.episodenames = episodeTitles;
        this.latestEpisode = latestEpisode;
        this.poster = poster;
        this.title = title;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public List<String> getEpisodenames() {
        return episodenames;
    }

    public String getLatestEpisode() {
        return latestEpisode;
    }

    public String getPoster() {
        return poster;
    }

    public String getTitle() {
        return title;
    }
}
