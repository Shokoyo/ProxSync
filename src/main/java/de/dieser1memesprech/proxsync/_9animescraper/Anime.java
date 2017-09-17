package de.dieser1memesprech.proxsync._9animescraper;

import de.dieser1memesprech.proxsync._9animescraper.Exceptions.No9AnimeUrlException;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync._9animescraper.util.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Anime {
    private String title;
    private String source;
    private Document document;
    private int episodeCount;
    private Map<String, Episode> episodeMap = new HashMap<String, Episode>();
    private AnimeSearchObject animeSearchObject;

    public Anime(String url) {
        this.source = HtmlUtils.getHtmlContent(url);
        this.document = Jsoup.parse(source);
        this.title = getTitleString();
        this.episodeCount = getEpisodeCountInt();
        this.animeSearchObject = search(title).get(0);
    }

    private String getTitleString() {
        return document.select("h1[class=title]").first().text();
    }

    public static List<AnimeSearchObject> search(String keyword) {
        keyword = keyword.replaceAll(" ", "%20");
        String url = Configuration.instance.BASE_URL + "/search?keyword=" + keyword;
        String content = HtmlUtils.getHtmlContent(url);
        return parseSearchMulti(content);
    }

    private static List<AnimeSearchObject> parseSearchMulti(String data) {
        List<AnimeSearchObject> animeList = new ArrayList<AnimeSearchObject>();
        Document doc = Jsoup.parse(data);

        Elements items = doc.select("div[class=item]");

        for (Element item : items) {
            animeList.add(parseSearchSingle(item));
        }
        return animeList;
    }

    private static AnimeSearchObject parseSearchSingle(Element item) {
        Element img = item.select("img").first();
        Element nameAnchor = item.select("a[class=name]").first();
        Element lang = item.select("div[class=lang]").first();
        String langStr = lang == null ? "sub" : lang.text();
        return new AnimeSearchObject(nameAnchor.text(), nameAnchor.attr("href"), langStr.toLowerCase(), Configuration.instance.SITE_NAME, img.attr("src"));
    }

    private int getEpisodeCountInt() {
        int count = -1;
        Elements servers = document.select("div[class=server row");
        for (Element server : servers) {
            if (server.attr("data-id").equals("30")) {
                Elements episodes = server.select("li");
                count = episodes.size();
            }
        }
        return count;
    }

    public Episode getEpisodeObject(String url) throws No9AnimeUrlException {
        String id = "";
        if (Pattern.matches(Configuration.instance.BASE_URL + "/watch/(.*)", url)) {
            if (isEpisodeLink(url)) {
                String episodeId = url.substring(url.lastIndexOf('/') + 1);
                System.out.println("Episode url provided: " + episodeId);
                if (!episodeMap.containsKey(episodeId)) {
                    Elements servers = document.select("div[class=server row");
                    Element body = document.select("body").first();
                    String ts = body.attr("data-ts");
                    String update = "0";
                    for (Element server : servers) {
                        if (server.attr("data-id").equals("30")) {
                            Elements episodes = server.select("li");
                            for (Element elEpisode : episodes) {
                                Element anchor = elEpisode.select("a").first();
                                id = anchor.attr("data-id");
                                if (episodeId.equals(id)) {
                                    Episode episode = parseServerSingleEpisode(elEpisode, ts, update, server.attr("data-id"));
                                    episodeMap.put(episodeId, episode);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                return episodeMap.get(episodeId);
            } else {
                System.out.println("Anime url provided");
                Elements servers = document.select("div[class=server row");
                Element body = document.select("body").first();
                String ts = body.attr("data-ts");
                String update = "0";
                for (Element server : servers) {
                    if (server.attr("data-id").equals("30")) {
                        Elements episodes = server.select("li");
                        for (Element elEpisode : episodes) {
                            Element anchor = elEpisode.select("a").first();
                            id = anchor.attr("data-id");
                            Episode episode = parseServerSingleEpisode(elEpisode, ts, update, server.attr("data-id"));
                            episodeMap.put(id, episode);
                            break;
                        }
                    }
                    break;
                }
            }
            return episodeMap.get(id);
        }
        throw new No9AnimeUrlException();
    }

    public Episode getEpisodeObject(String url, int episodeNum) {
        String id = "";
        Elements servers = document.select("div[class=server row");
        Element body = document.select("body").first();
        String ts = body.attr("data-ts");
        String update = "0";
        for (Element server : servers) {
            if (server.attr("data-id").equals("30")) {
                Elements episodes = server.select("li");
                for (Element elEpisode : episodes) {
                    Element anchor = elEpisode.select("a").first();
                    int epNum = Integer.parseInt(anchor.text());
                    System.out.println(epNum);
                    if (epNum == episodeNum) {
                        id = anchor.attr("data-id");
                        System.out.println(id);
                        if (!episodeMap.containsKey(id)) {
                            Episode episode = parseServerSingleEpisode(elEpisode, ts, update, id);
                            episodeMap.put(id, episode);
                        }
                        break;
                    }

                }
            }
        }
        return id.equals("") ? null : episodeMap.get(id);
    }

    private Episode parseServerSingleEpisode(Element elEpisode, String ts, String update, String serverid) {
        Element anchor = elEpisode.select("a").first();
        String id = anchor.attr("data-id");
        Episode episode = new Episode(id, anchor.text(), scrapeEpisodeInfo(id, ts, update, serverid));
        return episode.getSources() == null ? null : episode;
    }

    private String scrapeEpisodeInfo(String id, String ts, String update, String serverid) {
        String url = Configuration.instance.INFO_API_URL + "?ts=" + ts + "&_=" + _9AnimeUrlExtender.getExtraUrlParameter(id, ts, update, serverid) + "&id=" + id + "&server=" + serverid + "&update=" + update;
        String content = HtmlUtils.getHtmlContent(url);
        // TODO
        return content;
    }

    public AnimeSearchObject getAnimeSearchObject() {
        return animeSearchObject;
    }

    public Map<String, Episode> getEpisodeMap() {
        return episodeMap;
    }

    public String getTitle() {
        return title;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public boolean isEpisodeLink(String url) {
        return Pattern.matches(Configuration.instance.BASE_URL + "/watch/(.*[/])(.*)", url);
    }
}
