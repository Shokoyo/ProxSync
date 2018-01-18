package de.dieser1memesprech.proxsync.database;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jeremias on 22.09.2017.
 */
public class DatabaseTest {
    private boolean setup = false;
    private final static String SCHOKI_ID = "VMivZ0koAPh9Q3HC3UFWgn5CZ1n1";

    private void setUp() {
        try {
            // Fetch the service account key JSON file contents
            String auth =
                    "{\n" +
                            "  \"type\": \"service_account\",\n" +
                            "  \"project_id\": \"proxsync\",\n" +
                            "  \"private_key_id\": \"008e29b30a2f8998f999cc6d0033fae06aafa04b\",\n" +
                            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCt69Qog89mUx+v\\nDqR2sRdob9xytLlYKFDKszG4serxbnjosm6j3yumZJaPewpJSzJAK8YyIUnzcDG1\\nznAhMAklS6FKdqOlEwv5VVjsrDue7RQaqA4LkYchqbn8EsrTl/4Q5wqtKrUhQwD2\\nmwz6+U3m6KOvo04eQ8LI9oBkOanj2iLLR1lxuF/JR/n2iqG9UTRxmAFC/bxZwJDx\\nBArXyJ0KRBjsqTKhUmlaag1eVjmxPba9gNtZsP99s70cmTTgJRPbtf9fB+1HnU+B\\n6+BUXHTo17UQawUQ2+/TlKvkAHuIvK8tDjkHRaTDMTmKJsSGG1O/KlH9V1RoryYx\\nbXhZIkBJAgMBAAECggEAB1tTNpbYwN8hlyHl7WAGpElzCDvCc/WvRoZiQzSXbQ37\\nZFBujf+kf4iAC+KsUoxb5vX9ug90FYLjP2mo5DvSxIvt7yc/OyjSmAHhszqCgnGh\\n05Ip7JhZIHXm+EAtPy+4igDT5Uj0r3WqRpl+vjupwsDdclJUksLSrEstBCWiR5Et\\nKvCEpn3k7Zn34I40SojE6yWyDuvCxGXluO01oej7DGbKZL9oUQFJ+IkOXJmHH89F\\nkkc4O2ayDZqscUR7Y5GLHHtCSYYkMvwt2Ltrlb3SYqAnm+KYCxLJHP4CIGq9ICrh\\nH0c4X6w7v5Itxwt25F5K3sAtFo7cjdG7SH+mytT7fwKBgQDk3/mZtlipHw+Fkm8B\\npqGgGlnSh6tXvZtTj4tDz3jVIhiQV2IdnJXLLpEmbphz5ueiICo/Hr/fidt4EoS9\\nUdoFDo/NP7BmdiIhVrBA5wDB9mgHQsjjM3G5eIBVXofm1iAlA5CQmQn5M8obSL09\\n8zx3IWi85pIOV4vhMPy4jt2AlwKBgQDCiJFuQCYQ31wx2mllMheBI/MCRYEeTd3Y\\nQxyIgLiZGRz6IpIjdqLyn35R1B/ewRx3nvFh00YyEijiR7puGd2oIAbdkYUEeZAo\\nlxGrDEOxThYkhsT4DBizA2p8f4L/JrDMZAmoGhSB0nvhHenU5iZseQdddZWY3qTe\\nUmdtFHWCHwKBgH1t8ZA3ymcEyrDdC1DQTQs85bTm/RPcqV8l76B99y52vRd9jBvG\\nUKFFJ+7z9UCvbon/Lqfg5i5PkqM/ItfOH7ldvZkyKi4813+FzpoC/vtNWr1/8C/z\\nZYPLcilW4Qu0lXEzfup/tGOJ6l0BhBSLVLvE521+vtR1TgdfnWBXizSJAoGAejU3\\nP463GzRmg78VJQiR8YP2/r9RPQtnyZtnim1rCmrXZGBhdDvsZAVEYv2iw9qHGrLU\\nK0OBvqTf4D91ZAOF1z+/cGQhr0z6/jThWZZL1FAc4vdzN+zJe3Qy6mFklwvkV5Kk\\n6mDv7xqezi5vnlqzWsOxl8ntn+qGolAcP09x/rsCgYEAvXSBfpnlLKxwcWfgUVYg\\nd87SIkIyL/qQwfArMOisv/o976nm1vVDm/DFJxjA4tg87Stir+3d0cEUFJFhW4E8\\nDTa5P+QJms6I7DHeVo/gYpY34b1y+R0Lt4FtNKIFhW6GTO1YiWQ0zisNMJbsUrgN\\nWl/sAXDsYTQ63Na8rS52L6E=\\n-----END PRIVATE KEY-----\\n\",\n" +
                            "  \"client_email\": \"firebase-adminsdk-2dcdz@proxsync.iam.gserviceaccount.com\",\n" +
                            "  \"client_id\": \"106320872125258738960\",\n" +
                            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                            "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
                            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-2dcdz%40proxsync.iam.gserviceaccount.com\"\n" +
                            "}\n";
            InputStream serviceAccount = new ByteArrayInputStream(auth.getBytes());

            // Initialize the app with a service account, granting admin privileges
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl("https://proxsync.firebaseio.com")
                    .build();
            try {
                FirebaseApp.initializeApp(options);
            } catch(IllegalStateException e) {

            }
            Database.database = FirebaseDatabase.getInstance();
            System.out.println("Database initialized");
        } catch (IOException e) {
            System.out.println("Error during Firebase initialization");
            e.printStackTrace();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
    }

    @org.junit.Test
    public void addAnimeinfoToDatabase() {
        setUp();
        List<String> epList = new ArrayList<>();
        epList.add("Episode 1");
        epList.add("Episode 2");
        Database.addAnimeinfoToDatabase("test-anime.test123", "Test123", epList);
        sleep();
        DataSnapshot data = Database.getDataFromDatabase("anime/animeinfo/test-anime-test123");
        assertEquals("test-anime-test123", data.getKey());
        assertEquals("Test123", data.child("title").getValue());
        assertEquals(epList, data.child("episodenames").getValue());
    }

    @org.junit.Test
    public void getWatchlistObjectFromDatabase() throws Exception {
        setUp();
        Watchlist watchlist = Database.getWatchlistObjectFromDatabase("test-user");
        List<WatchlistEntry> list = watchlist.getWatching();
        assertEquals("Test123", list.get(0).getTitle());
    }

    @org.junit.Test
    public void updateAnimeInfo() throws Exception {
        setUp();
        Database.updateAnimeInfo("test-anime.test123", "2", "12", "http://test-site.de/test-image.jpg");
        sleep();
        DataSnapshot data = Database.getDataFromDatabase("anime/animeinfo/test-anime-test123");
        System.out.println(data.getValue());
        assertEquals("2", data.child("latestEpisode").getValue());
        assertEquals("12", data.child("episodeCount").getValue());
        assertEquals("http://test-site.de/test-image.jpg", data.child("poster").getValue());
    }

    public void setAvatar() throws Exception {
        setUp();
        Database.setAvatar("test-user", "http://test-site.de/test-image.jpg");
        sleep();
        DataSnapshot data = Database.getDataFromDatabase("users/test-user/avatar");
        assertEquals("http://test-site.de/test-image.jpg", data.getValue());
    }

    @org.junit.Test
    public void getBannerFromDatabase() throws Exception {
        setUp();
        setBanner();
        assertEquals("http://test-site.de/test-image.jpg", Database.getBannerFromDatabase("test-user"));
    }

    @org.junit.Test
    public void getAvatarFromDatabase() throws Exception {
        setUp();
        setAvatar();
        assertEquals("http://test-site.de/test-image.jpg", Database.getAvatarFromDatabase("test-user"));
    }

    public void setBanner() throws Exception {
        setUp();
        Database.setBanner("test-user", "http://test-site.de/test-image.jpg");
        sleep();
        DataSnapshot data = Database.getDataFromDatabase("users/test-user/banner");
        assertEquals("http://test-site.de/test-image.jpg", data.getValue());
    }

    @org.junit.Test
    public void getAnimeEntryFromDatabase() throws Exception {
        setUp();
        AnimeEntry test123 = Database.getAnimeEntryFromDatabase("test-anime.test123");
        assertEquals("Test123", test123.getTitle());
        assertEquals("[Episode 1, Episode 2]", test123.getEpisodenames().toString());
        assertEquals("http://test-site.de/test-image.jpg", test123.getPoster());
        assertEquals("12", test123.getEpisodeCount());
        assertEquals("2", test123.getLatestEpisode());
    }

    @org.junit.Test
    public void getEpisodeTitleFromDatabase() throws Exception {
        setUp();
    }

    @org.junit.Test
    public void addToWatchlistImporter() throws Exception {
        setUp();
    }

    @org.junit.Test
    public void getWatchlistEntry() throws Exception {
        setUp();
    }

    @org.junit.Test
    public void getWatchlistRating() throws Exception {
        setUp();
    }

    @org.junit.Test
    public void addToWatchlist() throws Exception {
        setUp();
        Database.addToWatchlist("test-anime-test123", "1", "watching", "test-user", null);
        sleep();
        DataSnapshot data = Database.getDataFromDatabase("users/test-user/watchlist/test-anime-test123");
        assertEquals("1", data.child("episode").getValue());
        assertEquals("watching", data.child("status").getValue());
        assertEquals("test-anime-test123", data.child("key").getValue());
    }

}