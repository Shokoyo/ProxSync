package de.dieser1memesprech.proxsync.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class Watchlist {
    private List<WatchlistEntry> watching;
    private List<WatchlistEntry> completed;
    private List<WatchlistEntry> planned;

    public Watchlist() {
        this.watching = new ArrayList<WatchlistEntry>();
        this.completed = new ArrayList<WatchlistEntry>();
        this.planned = new ArrayList<WatchlistEntry>();
    }

    public List<WatchlistEntry> getWatching() {
        return watching;
    }

    public List<WatchlistEntry> getCompleted() {
        return completed;
    }

    public List<WatchlistEntry> getPlanned() {
        return planned;
    }
}
