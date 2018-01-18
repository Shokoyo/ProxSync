package de.dieser1memesprech.proxsync._9animescraper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync._9animescraper.util.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.validation.constraints.Null;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Episode {
    private String id;
    private String episodeName;
    private String episodeUrl;
    private String sourceUrl;

    public Episode(String id, String episodeName, String episodeUrl) {
        this.id = id;
        this.episodeName = episodeName;
        this.episodeUrl = episodeUrl;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public String getId() {
        return id;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public String getSourceUrl() {
        if (sourceUrl == null) {
            sourceUrl = scrapeSource();
        }
        System.out.println(sourceUrl);
        return sourceUrl;
    }

    public String scrapeSource() {
        String sourceUrl = "";
        Document document = Jsoup.parse(HtmlUtils.getHtmlContent(episodeUrl/*.substring(0, episodeUrl.lastIndexOf("/"))*/));
        Elements servers = document.select("div[class=server row");
        Element body = document.select("body").first();
        String ts = body.attr("data-ts");
        ts = _9AnimeUrlExtender.decodeTs(ts);
        System.out.println(ts);
        String update = "0";
        for (Element server : servers) {
            //currently server F4
            if (server.attr("data-id").equals(Configuration.instance.getStreamServerId())) {
                Elements episodes = server.select("li");
                for (Element elEpisode : episodes) {
                    Element anchor = elEpisode.select("a").first();
                    String id = anchor.attr("data-id");
                    String serverId = server.attr("data-id");
                    if (episodeUrl.contains(id)) {
                        sourceUrl = parseServerSingleEpisode(elEpisode, ts, update, serverId);
                        if (sourceUrl.contains("openload")) {
                            sourceUrl = scrapeOpenload(sourceUrl);
                        } else if (sourceUrl.contains("rapidvideo")) {
                            sourceUrl = scrapeRapid(sourceUrl);
                        }
                        break;
                    }
                }
                break;
            }
        }
        /*if(sourceUrl.equals("")) {
            for (Element server : servers) {
                if (server.attr("data-id").equals("33")) {
                    Elements episodes = server.select("li");
                    for (Element elEpisode : episodes) {
                        Element anchor = elEpisode.select("a").first();
                        String id = anchor.attr("data-id");
                        if (episodeUrl.contains(id)) {
                            String embedUrl =  parseServerSingleEpisode(elEpisode, ts, update, id);
                            System.out.println(embedUrl);
                            sourceUrl = scrapeRapid(embedUrl);
                            break;
                        }
                    }
                    break;
                }
            }
        }*/
        return sourceUrl;
    }

    private String parseServerSingleEpisode(Element elEpisode, String ts, String update, String serverid) {
        Element anchor = elEpisode.select("a").first();
        String id = anchor.attr("data-id");
        return scrapeEpisodeInfo(id, ts, update, serverid);
    }

    private String scrapeEpisodeInfo(String id, String ts, String update, String serverid) {
        String url = Configuration.instance.INFO_API_URL + "?ts=" + ts + "&_=" + _9AnimeUrlExtender.getExtraUrlParameter(id, ts, update, serverid) + "&id=" + id + "&server=" + serverid + "&update=" + update;
        String content = HtmlUtils.getHtmlContent(url);
        System.out.println(url);
        System.out.println(content);
        if (content.contains("rapidvideo") || content.contains("openload")) {
            return scrapeEmbed(content);
        } else {
            return scrapeSourceUrl(content);
        }
    }

    private String scrapeOpenload(String embedUrl) {
        String iFrameContent = HtmlUtils.getHtmlContent(embedUrl);
        System.out.println(iFrameContent);
        //Document iFrameDocument = Jsoup.parse(iFrameContent);
        //Element vidElem = iFrameDocument.select("video").first();
        //episodeUrl = vidElem.select("source").attr("src");
        //System.out.println(episodeUrl);
        return episodeUrl;
    }

    private String scrapeEmbed(String content) {
        JsonElement jsonElementSource = new JsonParser().parse(content);
        JsonObject jsonObjectSource = jsonElementSource.getAsJsonObject();
        return jsonObjectSource.get("target").getAsString();
    }

    private String scrapeRapid(String embedUrl) {
        System.out.println(embedUrl);
        String iFrameContent = HtmlUtils.getHtmlContent(embedUrl + "?q=720p");
        System.out.println(iFrameContent);
        Document iFrameDocument = Jsoup.parse(iFrameContent);
        Element vidElem = iFrameDocument.select("video").first();
        episodeUrl = vidElem.select("source").attr("src");
        System.out.println(episodeUrl);
        return episodeUrl;
    }

    private String scrapeSourceUrl(String content) {
        JsonElement jsonElementSource = new JsonParser().parse(content);
        JsonObject jsonObjectSource = jsonElementSource.getAsJsonObject();
        String grabber = jsonObjectSource.get("grabber").getAsString();
        if(Configuration.instance.getStreamServerId().equals("33")) {
            episodeUrl = _9AnimeUrlExtender.decodeExtraParameter(jsonObjectSource.get("target").getAsString());
        } else {
            JsonObject params = jsonObjectSource.getAsJsonObject("params");
            String token = params.get("token").getAsString();
            String options = params.get("options").getAsString();

            token = _9AnimeUrlExtender.decodeExtraParameter(token);
            options = _9AnimeUrlExtender.decodeExtraParameter(options);

            String url;

            url = grabber + "&token=" + token + "&options=" + options + "&id=" + params.get("id").getAsString();
            System.out.println(url);
            String episodeUrls = HtmlUtils.getHtmlContent(url);
            System.out.println(episodeUrls);
            JsonElement jsonElementUrls = new JsonParser().parse(episodeUrls);
            JsonObject jsonObjectUrls = jsonElementUrls.getAsJsonObject();
            JsonArray jsonArrayUrlsData = jsonObjectUrls.getAsJsonArray("data");
            try {
                episodeUrl = jsonArrayUrlsData.get(jsonArrayUrlsData.size() - 1).getAsJsonObject().get("file").getAsString();
            } catch (NullPointerException e) {
                e.printStackTrace();
                episodeUrl = "";
            }
        }
        return episodeUrl;
    }
}
