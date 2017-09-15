package de.dieser1memesprech.proxsync._9animescraper;

public class Episode {
    private String id;
    private String epNum;
    private String sources;
    private int epNumInt;

    public Episode(String id, String epNum, String sources) {
        this.id = id;
        this.epNum = epNum;
        this.sources = sources;
        this.epNumInt = Integer.parseInt(epNum);
    }

    public String getSources() {
        return sources;
    }

    public String getId() {
        return id;
    }

    public String getEpNum() {
        return epNum;
    }

    public int getEpNumInt() {
        return epNumInt;
    }
}
