package de.dieser1memesprech.proxsync.database;

import java.util.List;

/**
 * Created by Jeremias on 22.09.2017.
 */
public class AnimeEntry {
    String episodeCount;
    String[] episodeTitles;
    int latestEpisode;
    String poster;
    String title;

    public AnimeEntry(String episodeCount, String[] episodeTitles, int latestEpisode, String poster, String title) {
        this.episodeCount = episodeCount;
        this.episodeTitles = episodeTitles;
        this.latestEpisode = latestEpisode;
        this.poster = poster;
        this.title = title;
    }
}
