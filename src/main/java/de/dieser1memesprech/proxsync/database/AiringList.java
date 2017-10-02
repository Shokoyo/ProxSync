package de.dieser1memesprech.proxsync.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremias on 01.10.2017.
 */
public class AiringList {
    private List<AiringEntry> tvList;
    private List<AiringEntry> movieList;
    private List<AiringEntry> shortList;
    private List<AiringEntry> ovaList;

    public AiringList() {
        tvList = new ArrayList<>();
        movieList = new ArrayList<>();
        shortList = new ArrayList<>();
        ovaList = new ArrayList<>();
    }

    public List<AiringEntry> getTvList() {
        return tvList;
    }

    public List<AiringEntry> getMovieList() {
        return movieList;
    }

    public List<AiringEntry> getShortList() {
        return shortList;
    }

    public List<AiringEntry> getOvaList() {
        return ovaList;
    }
}
