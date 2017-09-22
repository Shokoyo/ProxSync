package de.dieser1memesprech.proxsync._9animescraper.util;

import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AnimeUtils {
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
