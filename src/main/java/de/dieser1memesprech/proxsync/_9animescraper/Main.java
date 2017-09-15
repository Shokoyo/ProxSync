package de.dieser1memesprech.proxsync._9animescraper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String SITE_NAME = "9anime.to";

    public static final String BASE_URL = "https://9anime.to";
    public static final String SEARCH_URL = BASE_URL + "/search";
    public static final String INFO_API_URL = BASE_URL + "/ajax/episode/info";

    /**
     * Gets the URL of the video of episode episodeNum from an anime-link
     *
     * @param animeUrl The URL to the anime
     * @param episodeNum The number of the episode
     * @return The video URL
     */
    public String getEpisodeOfAnime(String animeUrl, int episodeNum) {
        return getEpisodeUrlFromList(scrapeAllShowSources(animeUrl), episodeNum);
    }

    /**
     * Gets the URL of the video from an episode-link
     *
     * @param episodeUrl The URL to the specific Episode
     * @return The video URL
     */
    public String getEpisodeOfAnime(String episodeUrl) {
        return getEpisodeUrl(getEpisodeObjectFromUrl(episodeUrl));
    }

    private Episode getEpisodeObjectFromUrl(String url) {
        String content = getHtmlContent(url);
        Document doc = Jsoup.parse(content);
        Elements servers = doc.select("div[class=server row");
        Element body = doc.select("body").first();
        String ts = body.attr("data-ts");
        String update = "0";
        for (Element server : servers) {
            if (server.attr("data-id").equals("30")) {
                Elements episodes = server.select("li");
                for (Element elEpisode : episodes) {
                    Episode episode = parseServerSingleEpisode(elEpisode, ts, update, server.attr("data-id"));
                    if (url.contains(episode.getId())) {
                        return episode;
                    }
                }
            }
        }
        return null;
    }

    private List<Anime> search(String keyword) {
        String url = BASE_URL + "/search?keyword=" + keyword;
        String content = getHtmlContent(url);
        return parseSearchMulti(content);
    }

    private String getHtmlContent(String url) {
        String content = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                content = EntityUtils.toString(entity, "UTF-8");
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private List<Anime> parseSearchMulti(String data) {
        List<Anime> animeList = new ArrayList<Anime>();
        Document doc = Jsoup.parse(data);

        Elements items = doc.select("div[class=item]");

        for (Element item : items) {
            animeList.add(parseSearchSingle(item));
        }
        return animeList;
    }

    private Anime parseSearchSingle(Element item) {
        Element img = item.select("img").first();
        Element nameAnchor = item.select("a[class=name]").first();
        Element lang = item.select("div[class=lang]").first();
        String langStr = lang == null ? "sub" : lang.text();
        return new Anime(nameAnchor.text(), nameAnchor.attr("href"), langStr.toLowerCase(), SITE_NAME, img.attr("src"));
    }

    private List<List<Episode>> scrapeAllShowSources(String url) {
        String content = getHtmlContent(url);
        Document doc = Jsoup.parse(content);
        Element body = doc.select("body").first();
        String ts = body.attr("data-ts");
        String update = "0";
        List<List<Episode>> servers = scrapeAllServers(content, ts, update);
        return servers;
    }

    private List<List<Episode>> scrapeAllServers(String content, String ts, String update) {
        Document doc = Jsoup.parse(content);
        List<List<Episode>> episodeListList = new ArrayList<List<Episode>>();
        Elements servers = doc.select("div[class=server row");
        for (Element server : servers) {
            if (server.attr("data-id").equals("30")) {
                List<Episode> episodeList = new ArrayList<Episode>(parseServerEpisodes(server, ts, update));
                if (episodeList != null) {
                    episodeListList.add(episodeList);
                }
            }
        }
        return episodeListList;
    }

    private List<Episode> parseServerEpisodes(Element server, String ts, String update) {
        List<Episode> episodeList = new ArrayList<Episode>();
        Elements episodes = server.select("li");
        for (Element elEpisode : episodes) {
            Episode episode = parseServerSingleEpisode(elEpisode, ts, update, server.attr("data-id"));
            if (episode != null) {
                episodeList.add(episode);
            }
        }
        return episodeList;
    }

    private Episode parseServerSingleEpisode(Element elEpisode, String ts, String update, String serverid) {
        Element anchor = elEpisode.select("a").first();
        String id = anchor.attr("data-id");
        Episode episode = new Episode(id, anchor.text(), scrapeEpisodeInfo(id, ts, update, serverid));
        return episode.getSources() == null ? null : episode;
    }

    private String scrapeEpisodeInfo(String id, String ts, String update, String serverid) {
        String url = INFO_API_URL + "?ts=" + ts + "&_=" + _9AnimeUrlExtender.getExtraUrlParameter(id, ts, update, serverid) + "&id=" + id + "&server=" + serverid + "&update=" + update;
        String content = getHtmlContent(url);
        // TODO
        return content;
    }

    private String getEpisodeUrlFromList(List<List<Episode>> episodeListList, int episodeNum) {
        String episodeUrl = "";
        if (episodeListList.get(0).size() > episodeNum) {
            Episode episode = episodeListList.get(0).get(episodeNum - 1);
            episodeUrl = getEpisodeUrl(episode);
        }
        return episodeUrl;
    }

    private String getEpisodeUrl(Episode episode) {

        String episodeJson = episode.getSources();
        JsonElement jsonElementSource = new JsonParser().parse(episodeJson);
        JsonObject jsonObjectSource = jsonElementSource.getAsJsonObject();
        String grabber = jsonObjectSource.get("grabber").getAsString();
        JsonObject params = jsonObjectSource.getAsJsonObject("params");
        String token = params.get("token").getAsString();
        String url = grabber + "&token=" + token;
        String episodeUrls = getHtmlContent(url);

        JsonElement jsonElementUrls = new JsonParser().parse(episodeUrls);
        JsonObject jsonObjectUrls = jsonElementUrls.getAsJsonObject();
        JsonArray jsonArrayUrlsData = jsonObjectUrls.getAsJsonArray("data");

        String episodeUrl = jsonArrayUrlsData.get(jsonArrayUrlsData.size()-1).getAsJsonObject().get("file").getAsString();

        return episodeUrl;
    }
}
