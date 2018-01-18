package de.dieser1memesprech.proxsync._9animescraper.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AnimeUtils {
    public static List<AnimeSearchObject> search(String keyword) {
        keyword = keyword.replaceAll(" ", "+");
        String url = Configuration.instance.SEARCH_URL + keyword;
        System.out.println(url);
        String content = HtmlUtils.getHtmlContent(url);
        System.out.println(content);
        return parseMasterSearch(content);
    }

    public static String makeDirectLinkForNameAndEpisode(String name, int episode) {
        String content = HtmlUtils.getHtmlContent(Configuration.instance.DIRECT_LINK_API_URL + "/" + name + "/" + episode);
        String url = "";
        System.out.println(content);
        try {
            JsonArray jsonElements = new JsonParser().parse(content).getAsJsonArray();
            url = jsonElements.get(0).getAsJsonObject().get("url_direct").getAsString();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static List<AnimeSearchObject> parseMasterSearch(String json) {
        List<AnimeSearchObject> res = new ArrayList<>();
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonArray jsonElements = jsonElement.getAsJsonArray();
        for(JsonElement element: jsonElements) {
            JsonObject object = element.getAsJsonObject();
            String title = object.get("title").getAsString();
            String slug = object.get("slug").getAsString();
            int id = object.get("id").getAsInt();
            JsonObject posterObject = object.get("poster").getAsJsonObject();
            String poster = "https://cdn.masterani.me/" + posterObject.get("path").getAsString() +
                    posterObject.get("file").getAsString();
            AnimeSearchObject searchObject = new AnimeSearchObject(title, slug, id, poster);
            res.add(searchObject);
        }
        return res;
    }

    public static List<AnimeSearchObject> updatedSearch(String url) {
        return parseSearchMulti(HtmlUtils.getHtmlContent(url));
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
        Element status = item.select("div[class=status]").first();
        String episodeCount = "1";
        String lastEpisode = "1";
        if(status != null && !"".equals(status)) {
            String[] statusArray = status.text().split("/");
            if(statusArray.length == 2) {
                episodeCount = statusArray[1];
                lastEpisode = statusArray[0];
            }
        }
        String langStr = lang == null ? "sub" : lang.text();
        return new AnimeSearchObject(nameAnchor.text(), nameAnchor.attr("href"), langStr.toLowerCase(),
                Configuration.instance.SITE_NAME, img.attr("src"), episodeCount, lastEpisode);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
