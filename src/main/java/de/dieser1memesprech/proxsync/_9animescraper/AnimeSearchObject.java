package de.dieser1memesprech.proxsync._9animescraper;

public class AnimeSearchObject {

    private String title;
    private String link;
    private String language;
    private String host;
    private String poster;
    private int lastEpisode;
    private int episodeCount;

    public AnimeSearchObject(String title, String link, String language, String host, String poster, int lastEpisode, int episodeCount) {
        this.title = title;
        this.link = link;
        this.language = language;
        this.host = host;
        this.poster = poster;
        this.lastEpisode = lastEpisode;
        this.episodeCount = episodeCount;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getPoster() {
        return poster;
    }

    public String getHost() {
        return host;
    }

    public int getLastEpisode() {
        return lastEpisode;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }
}
