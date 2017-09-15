package de.dieser1memesprech.proxsync._9animescraper;

public class Episode {
    private String id;
    private String epNum;
    private String sources;

    public Episode(String id, String epNum, String sources) {
        this.id = id;
        this.epNum = epNum;
        this.sources = sources;
    }

    public String getSources() {
        return sources;
    }

    public String getId() {
        return id;
    }
}
