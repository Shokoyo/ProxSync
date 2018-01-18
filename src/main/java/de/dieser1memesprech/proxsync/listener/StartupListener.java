package de.dieser1memesprech.proxsync.listener;

/**
 * Created by Jeremias on 22.09.2017.
 */

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync.database.Database;
import de.dieser1memesprech.proxsync.util.AiringUpdater;
import de.dieser1memesprech.proxsync.util.NotificationUpdater;
import net.thegreshams.firebase4j.error.FirebaseException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartupListener implements ServletContextListener {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting up!");
        try {
            // Fetch the service account key JSON file contents
            InputStream serviceAccount = servletContextEvent
                    .getServletContext()
                    .getResourceAsStream("/WEB-INF/serviceAccountCredentials.json");

            // Initialize the app with a service account, granting admin privileges
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl("https://proxsync.firebaseio.com")
                    .build();
            FirebaseApp.initializeApp(options);
            Database.database = FirebaseDatabase.getInstance();
            System.out.println("Database initialized");
            Database.initStreamIdListener();

            Runnable updater = new NotificationUpdater();
            scheduler.scheduleAtFixedRate(new AiringUpdater(), 0, 1, TimeUnit.DAYS);
            scheduler.scheduleAtFixedRate(updater, 0, 5, TimeUnit.MINUTES);

            System.out.println("Notification updater initialized");
        } catch (IOException e) {
            System.out.println("Error during Firebase initialization");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(30, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            e.printStackTrace();
            scheduler.shutdownNow();
        }
        System.out.println("Scheduler shut down");
        FirebaseApp.getInstance().delete();
        System.out.println("Firebase shut down");
    }

    void makeRatingsLong() {
        try {
            String raw = Configuration.instance.getFirebase().get("users").getRawBody();
            System.out.println(raw);
            //Database.database.getReference("users").setValue(raw);
            for (int i = 0; i < 11; i++) {
                raw = raw.replaceAll("\"rating\":\"" + i + "\"", "\"rating\":" + i);
            }
            System.out.println(raw);
            Configuration.instance.getFirebase().put("users", raw);
        } catch(FirebaseException | UnsupportedEncodingException e) {

        }
    }

    void updateWatching() {
        DataSnapshot data = Database.getDataFromDatabase("users");
        for(DataSnapshot c: data.getChildren()) {
            if(c.hasChild("watchlist")) {
                for(DataSnapshot e: c.child("watchlist").getChildren()) {
                    System.out.println(c.getKey() + " " + e.getKey() + " " + e.child("episode").getValue());
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put(c.getKey(), e.child("episode").getValue());
                    Database.updateData("watching/" + e.getKey(), map);
                }
            }
        }
    }
}
