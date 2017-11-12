package de.dieser1memesprech.proxsync._9animescraper.config;

import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public enum Configuration {
    instance;

    public final String SITE_NAME = "9anime.is";
    public final String BASE_URL = "https://9anime.is";
    public final String SEARCH_URL = BASE_URL + "/search";
    public final String INFO_API_URL = BASE_URL + "/ajax/episode/info";
    private Firebase firebase = null;
    public CloseableHttpClient httpclient = HttpClients.createDefault();

    public Firebase getFirebase() {
        if (firebase == null) {
            try {
                firebase = new Firebase("https://proxsync.firebaseio.com/");
            } catch (FirebaseException e) {
                e.printStackTrace();
            }
        }
        return firebase;
    }
}
