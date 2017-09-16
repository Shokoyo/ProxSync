package de.dieser1memesprech.proxsync._9animescraper;

public class AnimeSearchObject {

    private String title;
    private String link;
    private String language;
    private String host;
    private String poster;

    public AnimeSearchObject(String title, String link, String language, String host, String poster) {
        this.title = title;
        this.link = link;
        this.language = language;
        this.host = host;
        this.poster = poster;
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
}
