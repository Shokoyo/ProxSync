package de.dieser1memesprech.proxsync.filter;

import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.util.AnimeUtils;
import de.dieser1memesprech.proxsync.database.Database;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jeremias on 03.10.2017.
 */
public class SearchFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            if(httpRequest.getMethod().equals("GET")) {
                String keyword = "";
                String uid = "";
                boolean doSearch = false;
                for(Map.Entry<String, String[]> entry : (Set<Map.Entry>) httpRequest.getParameterMap().entrySet()) {
                    if(entry.getKey().equals("search")) {
                        keyword = entry.getValue()[0];
                        doSearch = true;
                    }
                    if(entry.getKey().equals("uid")) {
                        uid = entry.getValue()[0];
                    }
                }
                if(doSearch) {
                    List<AnimeSearchObject> searchList = AnimeUtils.search(keyword);
                    JsonArray searchObject = createSearchJson(searchList, uid);
                    HttpServletResponse response = (HttpServletResponse) servletResponse;
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.getWriter().write(searchObject.toString());
                    response.getWriter().flush();
                    return;
                }
            }
        } catch(ClassCastException | ArrayIndexOutOfBoundsException e) {

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    JsonArray createSearchJson(List<AnimeSearchObject> l, String uid) {
        JsonProvider provider = JsonProvider.provider();
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        Object data = Database.getDataFromDatabase("users/" + uid + "/watchlist").getValue();
        Map<String, Object> dataMap = new LinkedHashMap<>();
        try {
            dataMap = (Map<String, Object>) data;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        for (AnimeSearchObject animeSearchObject : l) {
            jsonArray.add(Json.createObjectBuilder()
                    .add("title", animeSearchObject.getTitle())
                    .add("link", animeSearchObject.getLink())
                    .add("image", animeSearchObject.getPoster())
                    .add("lastEpisode", animeSearchObject.getLastEpisode())
                    .add("watchlist", -1)
                    .add("episodeCount", animeSearchObject.getEpisodeCount()));
        }
        javax.json.JsonArray array = jsonArray.build();
        return array;
    }

    private int getEpisodenumFromWatchlist(Map<String, Object> dataMap, String link) {
        int num = -1;
        String key = link.substring(link.lastIndexOf("/") + 1);
        key = key.replaceAll("\\.", "-");
        if (dataMap.size() > 0) {
            Map map = (Map) dataMap.get(key);
            if (map != null) {
                num = Integer.parseInt(map.get("episode").toString());
            }
        }
        return num;
    }
}
