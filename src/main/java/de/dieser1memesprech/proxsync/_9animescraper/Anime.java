package de.dieser1memesprech.proxsync._9animescraper;

public class Anime {

    private String title;
    private String link;
    private String language;
    private String host;
    private String poster;

    public Anime(String title, String link, String language, String host, String poster) {
        this.title = title;
        this.link = link;
        this.language = language;
        this.host = host;
        this.poster = poster;
    }

    public String getLink() {
        return link;
    }
}
