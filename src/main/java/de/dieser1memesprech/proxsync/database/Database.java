package de.dieser1memesprech.proxsync.database;

import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.error.JacksonUtilityException;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import net.thegreshams.firebase4j.service.Firebase;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static void testIt() {
    }

    public static void addToDB(String key, String title, List<String> res) {

        try {
            Map<String, Object> dataMapEpisodeNames = new LinkedHashMap<String, Object>();
            for (int i = 0; i < res.size(); i++) {
                dataMapEpisodeNames.put(Integer.toString(i), res.get(i));
            }

            Map<String, Object> dataMapAnimeInfo = new LinkedHashMap<String, Object>();
            dataMapAnimeInfo.put("title", title);
            dataMapAnimeInfo.put("episodenames", dataMapEpisodeNames);

            FirebaseResponse response = Configuration.instance.getFirebase().put("anime/animeinfo/" + key.replaceAll("\\.", "-"), dataMapAnimeInfo);
            System.out.println(response);
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JacksonUtilityException e) {
            e.printStackTrace();
        }
    }

    public static FirebaseResponse getEpisodeTitleFromDatabase(String key, int episode) {
        FirebaseResponse response = null;
        try {
            response = Configuration.instance.getFirebase().get("anime/animeinfo/" + key.replaceAll("\\.", "-") + "/episodenames/" + (episode - 1));
        } catch (FirebaseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }
}
