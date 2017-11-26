package de.dieser1memesprech.proxsync._9animescraper;

import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync._9animescraper.util.AnimeUtils;
import de.dieser1memesprech.proxsync._9animescraper.util.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Pattern;

public class Anime {
    private String title;
    private String source;
    private Document document;
    private String episodeCount;
    private List<Episode> episodeList;
    private AnimeSearchObject animeSearchObject;

    public Anime(String url) {
        this.source = HtmlUtils.getHtmlContent(url);
        this.document = Jsoup.parse(source);
        this.title = getTitleString();
        initializeEpisodeList();
        this.episodeCount = "" + getEpisodeCountInt();
        List<AnimeSearchObject> animeSearchObjectList = AnimeUtils.search(title);
        for (AnimeSearchObject animeSearchObject : animeSearchObjectList) {
            if (url.contains(animeSearchObject.getLink())) {
                this.animeSearchObject = animeSearchObject;
                this.episodeCount = animeSearchObject.getEpisodeCount();
                break;
            }
        }
    }

    private void initializeEpisodeList() {
        episodeList = new ArrayList<Episode>();
        Elements servers = document.select("div[class=server row");
        Element body = document.select("body").first();
        for (Element server : servers) {
            if (/*server.attr("data-id").equals("33") ||*/ server.attr("data-id").equals(Configuration.instance.getStreamServerId())) {
                Elements episodes = server.select("li");
                for (Element elEpisode : episodes) {
                    Element anchor = elEpisode.select("a").first();
                    String id = anchor.attr("data-id");
                    String name = anchor.text();
                    episodeList.add(new Episode(id, name, Configuration.instance.BASE_URL + anchor.attr("href")));
                }
                break;
            }
        }
        return;
    }

    private String getTitleString() {
        return document.select("h1[class=title]").first().text();
    }

    private int getEpisodeCountInt() {
        return getEpisodeList().size();
    }

    private Episode parseServerSingleEpisode(Element elEpisode, String ts, String update, String serverid) {
        Element anchor = elEpisode.select("a").first();
        String id = anchor.attr("data-id");
        Episode episode = new Episode(id, anchor.text(), scrapeEpisodeInfo(id, ts, update, serverid));
        return episode.getEpisodeUrl() == null ? null : episode;
    }

    private String scrapeEpisodeInfo(String id, String ts, String update, String serverid) {
        String url = Configuration.instance.INFO_API_URL + "?ts=" + ts + "&_=" + _9AnimeUrlExtender.getExtraUrlParameter(id, ts, update, serverid) + "&id=" + id + "&server=" + serverid + "&update=" + update;
        System.out.println(url);
        String content = HtmlUtils.getHtmlContent(url);
        // TODO
        System.out.println(content);
        return content;
    }

    public AnimeSearchObject getAnimeSearchObject() {
        return animeSearchObject;
    }

    public List<Episode> getEpisodeList() {
        return episodeList;
    }

    public String getTitle() {
        return title;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }

    public boolean isEpisodeLink(String url) {
        return Pattern.matches(Configuration.instance.BASE_URL + "/watch/(.*[/])(.*)", url);
    }
}
