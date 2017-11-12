package de.dieser1memesprech.proxsync.util;

import com.google.firebase.database.DataSnapshot;
import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.util.AnimeUtils;
import de.dieser1memesprech.proxsync.database.AnimeEntry;
import de.dieser1memesprech.proxsync.database.Database;
import de.dieser1memesprech.proxsync.database.Notification;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeremias on 22.09.2017.
 */
public class NotificationUpdater implements Runnable {
    @Override
    public void run() {
        System.out.println("Updating notifications");
        List<AnimeSearchObject> objects = AnimeUtils.updatedSearch("https://9anime.is/updated");
        for (AnimeSearchObject o : objects) {
            String key = o.getLink().substring(o.getLink().lastIndexOf("/") + 1);
            Map<String, String> watchingMap = Database.getWatchingList(key);
            if (!watchingMap.isEmpty()) {
                for (Map.Entry<String, String> entry : watchingMap.entrySet()) {
                    String uid = entry.getKey();
                    String episode = entry.getValue();
                    Notification currentNotification = Database.getNotification(uid, key);
                    boolean notify = false;
                    if (currentNotification != null && currentNotification.getLatestEpisode() != null) {
                        notify = true;
                    } else if (Integer.parseInt(episode) == Integer.parseInt(o.getLastEpisode()) - 1) {
                        notify = true;
                    }
                    if (notify) {
                        Database.addNotification(key, uid, o.getTitle(), o.getLastEpisode(), o.getEpisodeCount());
                    }
                }
            }
        }
        System.out.println("Notification update completed");
    }
}
