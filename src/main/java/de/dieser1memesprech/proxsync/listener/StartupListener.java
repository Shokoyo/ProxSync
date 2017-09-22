package de.dieser1memesprech.proxsync.listener;

/**
 * Created by Jeremias on 22.09.2017.
 */

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;

public class StartupListener implements ServletContextListener {

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

            System.out.println("Database initialized");
        } catch (IOException e) {
            System.out.println("Error during Firebase initialization");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
    }
}
