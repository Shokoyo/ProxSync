package de.dieser1memesprech.proxsync._9animescraper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dieser1memesprech.proxsync._9animescraper.util.HtmlUtils;

public class Episode {
    private String id;
    private String epNum;
    private String sources;
    private int epNumInt;
    private String episodeUrl;

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

    public String getEpisodeUrl() {
        if (episodeUrl == null) {
            String episodeJson = this.getSources();
            JsonElement jsonElementSource = new JsonParser().parse(episodeJson);
            JsonObject jsonObjectSource = jsonElementSource.getAsJsonObject();
            String grabber = jsonObjectSource.get("grabber").getAsString();
            JsonObject params = jsonObjectSource.getAsJsonObject("params");
            String token = params.get("token").getAsString();
            String url = grabber + "&token=" + token;
            String episodeUrls = HtmlUtils.getHtmlContent(url);
            JsonElement jsonElementUrls = new JsonParser().parse(episodeUrls);
            JsonObject jsonObjectUrls = jsonElementUrls.getAsJsonObject();
            JsonArray jsonArrayUrlsData = jsonObjectUrls.getAsJsonArray("data");
            episodeUrl = jsonArrayUrlsData.get(jsonArrayUrlsData.size() - 1).getAsJsonObject().get("file").getAsString();
        }
        return episodeUrl;
    }
}
