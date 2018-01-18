package de.dieser1memesprech.proxsync._9animescraper;

public class AnimeSearchObject {

    private String title;
    private int id;
    private String link;
    private String language;
    private String host;
    private String poster;
    private String lastEpisode;
    private String episodeCount;

    public AnimeSearchObject(String title, String link, String language, String host, String poster, String episodeCount, String currentEpisode) {
        this.title = title;
        this.link = link;
        this.language = language;
        this.host = host;
        this.poster = poster;
        this.lastEpisode = currentEpisode;
        this.episodeCount = episodeCount;
    }

    public AnimeSearchObject(String title, String link, int id, String poster) {
        this.title = title;
        this.id = id;
        this.link = link;
        this.language = "EngSub";
        this.host = "";
        this.poster = poster;
        this.lastEpisode = "0";
        this.episodeCount = "0";
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

    public String getLastEpisode() {
        return lastEpisode;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }
}
