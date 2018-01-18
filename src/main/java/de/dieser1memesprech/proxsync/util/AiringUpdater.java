package de.dieser1memesprech.proxsync.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dieser1memesprech.proxsync.database.AiringEntry;
import de.dieser1memesprech.proxsync.database.Database;
import org.apache.commons.text.WordUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeremias on 29.09.2017.
 */
public class AiringUpdater implements Runnable {
    private static final String query = "query($page: Int,$year: Int,$season: MediaSeason){\\n" +
            "  Page(page: $page) {\\n" +
            "    pageInfo {\\n" +
            "      total\\n" +
            "      perPage\\n" +
            "      currentPage\\n" +
            "      lastPage\\n" +
            "      hasNextPage\\n" +
            "    }\\n" +
            "    media(seasonYear: $year, season: $season, type: ANIME, sort: POPULARITY_DESC) {\\n" +
            "      id\\n" +
            "      duration\\n" +
            "      title {\\n" +
            "        romaji\\n" +
            "      }\\n" +
            "      format\\n" +
            "      startDate {\\n" +
            "        year\\n" +
            "        month\\n" +
            "        day\\n" +
            "      }\\n" +
            "      source\\n" +
            "      genres\\n" +
            "      episodes\\n" +
            "      coverImage {\\n" +
            "        large\\n" +
            "      }\\n" +
            "      averageScore\\n" +
            "      popularity\\n" +
            //"      youtubeId\\n" +
            "      description\\n" +
            "      studios {\\n" +
            "        nodes {\\n" +
            "          name\\n" +
            "        }\\n" +
            "      }\\n" +
            "    }\\n" +
            "  }\\n" +
            "}\\n";


    public void run() {
        try {
            List<AiringEntry> l = new ArrayList<>();
            int i = 1;
            boolean hasNextPage = true;
            while (hasNextPage) {
                hasNextPage = addPageContentToList(l, i);
                i++;
            }
            System.out.println("Adding " + l.size() + " airing entries");
            Database.addAiringInfo(l);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the entries in a given page to a given list of entries
     *
     * @param l    the list
     * @param page the page number
     * @return true iff the page is valid and is not the last page.
     */
    private boolean addPageContentToList(List<AiringEntry> l, int page) {
        String content = getPageContent(page);
        System.out.println(content);
        JsonElement el = new JsonParser().parse(content);
        boolean hasNextPage = false;
        try {
            JsonObject o = el.getAsJsonObject();
            JsonObject data = o.getAsJsonObject("data");
            JsonObject pageObject = data.getAsJsonObject("Page");
            JsonObject pageInfo = pageObject.getAsJsonObject("pageInfo");
            hasNextPage = pageInfo.get("hasNextPage").getAsBoolean();
            JsonArray entries = pageObject.getAsJsonArray("media");
            addJsonArrayToList(l, entries);
        } catch (IllegalStateException | ClassCastException e) {
            e.printStackTrace();
        }
        return hasNextPage;
    }

    private void addJsonArrayToList(List<AiringEntry> l, JsonArray entries) throws IllegalStateException, ClassCastException {
        for (JsonElement element : entries) {
            JsonObject object = element.getAsJsonObject();
            AiringEntry entry = new AiringEntry();
            entry.setId(object.get("id").getAsInt());
            if (!object.get("duration").isJsonNull()) {
                entry.setDuration(object.get("duration").getAsInt());
            } else {
                entry.setDuration(0);
            }
            entry.setTitle(object.get("title").getAsJsonObject().get("romaji").getAsString());
            entry.setFormat(object.get("format").getAsString());
            String res = "";
            if (!object.getAsJsonObject("startDate").get("year").isJsonNull()) {
                res += object.getAsJsonObject("startDate").get("year").getAsInt();
            }
            if (!object.getAsJsonObject("startDate").get("month").isJsonNull()) {
                res += object.getAsJsonObject("startDate").get("month").getAsInt();
            }
            if (!object.getAsJsonObject("startDate").get("day").isJsonNull()) {
                res += object.getAsJsonObject("startDate").get("day").getAsInt();
            }
            if (res.length() != 8) {
                res = "";
            }
            entry.setStartDate(res);
            String source = "";
            if(!object.get("source").isJsonNull()) {
                source = object.get("source").getAsString();
            }
            source = source.replaceAll("_", " ");
            source = WordUtils.capitalizeFully(source);
            entry.setSource(source);
            List<String> genreList = new ArrayList<>();
            for (JsonElement genre : object.getAsJsonArray("genres")) {
                genreList.add(genre.getAsString());
            }
            entry.setGenres(genreList);
            if (!object.get("episodes").isJsonNull()) {
                entry.setEpisodes(object.get("episodes").getAsInt());
            } else {
                entry.setEpisodes(0);
            }
            entry.setPoster(object.getAsJsonObject("coverImage").get("large").getAsString());
            entry.setPoster(entry.getPoster().replaceAll("\\\\", ""));
            if (!object.get("averageScore").isJsonNull()) {
                entry.setScore(object.get("averageScore").getAsInt());
            } else {
                entry.setScore(0);
            }
            if (!object.get("popularity").isJsonNull()) {
                entry.setPopularity(object.get("popularity").getAsInt());
            } else {
                entry.setPopularity(0);
            }
            /*if (!object.get("youtubeId").isJsonNull()) {
                entry.setYoutubeId(object.get("youtubeId").getAsString());
            } else {*/
                entry.setYoutubeId("");
            //}
            if(!object.get("description").isJsonNull()) {
                entry.setDescription(object.get("description").getAsString());
            } else {
                entry.setDescription("");
            }
            try {
                entry.setStudioName(object.getAsJsonObject("studios")
                        .getAsJsonArray("nodes")
                        .get(0)
                        .getAsJsonObject()
                        .get("name")
                        .getAsString());
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                entry.setStudioName("");
            }
            l.add(entry);
        }
    }

    private String getPageContent(int page) {
        int year = Year.now().getValue();
        String seasonString = getSeasonString().toUpperCase();
        if(seasonString.equals("WINTERNEXT")) {
            seasonString = "WINTER";
            year++;
        }
        String variables = "{" +
                "\"page\":" + page + "," +
                "\"year\":" + year + "," +
                "\"season\":\"" + seasonString + "\"}";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://graphql.anilist.co");
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        String bodyString = "{\"query\":\"" + query + "\",\"variables\":" + variables + "}";
        //String bodyString = "{\"query\":\"{\\n  Page(page: 1) {\\n    pageInfo {\\n      total\\n      perPage\\n      currentPage\\n      lastPage\\n      hasNextPage\\n    }\\n    media(seasonYear: 2017, season: FALL, type: ANIME, sort: POPULARITY_DESC) {\\n      id\\n      duration\\n      title {\\n        romaji\\n      }\\n      format\\n      startDate {\\n        year\\n        month\\n        day\\n      }\\n      source\\n      genres\\n      episodes\\n      coverImage {\\n        large\\n      }\\n      averageScore\\n      popularity\\n      youtubeId\\n      description\\n      studios {\\n        nodes {\\n          name\\n        }\\n      }\\n    }\\n  }\\n}\\n\",\"variables\":null,\"operationName\":null}";
        try {
            HttpEntity entity = new ByteArrayEntity((bodyString).getBytes("UTF-8"));
            post.setEntity(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = "";
        try {
            CloseableHttpResponse response = client.execute(post);
            content = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
        //return HtmlUtils.getHtmlContent("https://anilist.co/api/browse/anime?access_token=" + createAccessToken() + "&year=" + Year.now().getValue() + "&season=" + getSeasonString() + "&sort=popularity-desc&full_page=true");
    }

    private String getSeasonString() {
        int month = YearMonth.now().getMonthValue();
        int day = MonthDay.now().getDayOfMonth();
        String res;
        if (month >=12) {
            res = "winterNext";
        } else if(month > 8) {
            res = "fall";
        } else if (month > 5) {
            res = "summer";
        } else if (month > 2) {
            res = "spring";
        } else {
            res = "winter";
        }
        return res;
    }
}