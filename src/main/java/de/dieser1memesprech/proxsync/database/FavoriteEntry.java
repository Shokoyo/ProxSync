package de.dieser1memesprech.proxsync.database;

/**
 * Created by Jeremias on 23.09.2017.
 */
public class FavoriteEntry {
    private AnimeEntry entry;
    private String key;

    public FavoriteEntry(AnimeEntry entry, String key) {
        this.entry = entry;
        this.key = key;
    }

    public AnimeEntry getEntry() {
        return entry;
    }

    public String getKey() {
        return key;
    }
}
