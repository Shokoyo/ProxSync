package de.dieser1memesprech.proxsync.util;

import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.util.AnimeUtils;
import de.dieser1memesprech.proxsync.database.Database;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.List;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class ProxerImporter implements Runnable {
    String url;
    String uid;

    public ProxerImporter(String url, String uid) {
        this.url = url;
        this.uid = uid;
    }

    public void run() {
        Document doc = Jsoup.parse(url);
        Element body = doc.body();
        Elements table = body.getElementsByTag("table");
        parseTable(table.get(0), "completed");
        parseTable(table.get(1), "watching");
        parseTable(table.get(2), "planned");
        System.out.println("finished");
    }

    public void parseTable(Element table, String status) {
        Elements trElements = table.getElementsByTag("tr");
        for (int i = 2; i < trElements.size(); i++) {
            Element el = trElements.get(i);
            if(el.getElementsByTag("td").size() == 1) {
                continue;
            }
            String title = el.getElementsByTag("td").get(1).text();
            Element stars = el.getElementsByTag("td").get(3);
            int rating = 0;
            for (Element e : stars.getElementsByTag("img")) {
                if (!e.attributes().get("src").contains("stern_grau")) {
                    rating++;
                }
            }
            String progress = el.getElementsByTag("td").get(4).getAllElements().first().text();
            String[] eps = progress.split(" / ");
            List<AnimeSearchObject> searchResult = AnimeUtils.search(title);
            boolean success = false;
            String poster = "";
            String episodeCount = "";
            if (searchResult != null) {
                for (AnimeSearchObject a : searchResult) {
                    if (a.getTitle().toLowerCase().equals(title.toLowerCase())) {
                        success = true;
                        Database.addToWatchlistImporter(a.getLink().substring(a.getLink().lastIndexOf("/") + 1), eps[0], status, title, a.getPoster(), "" + a.getEpisodeCount(), uid, "" + rating);
                    }
                }
                if (!success) {
                    System.out.println("Failed for " + title);
                }
            } else {
                System.out.println("Failed for " + title);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("invalid arguments");
            System.exit(-1);
        }
        System.out.println(args[1]);
        File file = new File(args[0]);
        try {
            FileReader fileStream = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileStream);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            fileStream.close();
            bufferedReader.close();
            ProxerImporter importer = new ProxerImporter(builder.toString(), args[1]);
            System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(file.getParent() + "\\log.txt")), true));
            importer.run();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(-1);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
