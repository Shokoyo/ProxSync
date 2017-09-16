package de.dieser1memesprech.proxsync.user;

import com.google.gson.Gson;
import de.dieser1memesprech.proxsync._9animescraper.Anime;
import de.dieser1memesprech.proxsync._9animescraper.Episode;
import de.dieser1memesprech.proxsync._9animescraper.Exceptions.No9AnimeUrlException;
import de.dieser1memesprech.proxsync.websocket.UserSessionHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Room {
    private int episode;
    private Queue<Video> playlist;
    private static final String USER_AGENT = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13";
    private static final String ripLink = "http://i.imgur.com/eKmmyv1.mp4";
    private HashMap<Session, Boolean> readyStates = new HashMap<Session, Boolean>();
    private HashMap<Session, String> nameMap = new HashMap<Session, String>();
    private List<Session> sessions;
    private String video = "";
    private String _9animeLink = "";
    private Session host;
    private String id;
    private boolean playing = false;
    private boolean buffering = false;
    private boolean isDirectLink = false;
    private boolean autoNext = false;
    private String lastCookie = "";
    private CloseableHttpClient httpClient;
    private JsonNumber timestamp;
    private Random random = new Random();
    private Anime anime;

    public Room(Session host, String hostname, String roomName) {
        playlist = new LinkedList<Video>();
        this.host = host;
        if (roomName != null && !roomName.equals("")) {
            id = roomName;
        } else {
            do {
                id = Long.toHexString(Double.doubleToLongBits(Math.random()));
            } while (!RoomHandler.getInstance().checkId(id));
        }
        sessions = new LinkedList<Session>();
        this.addSession(host, hostname);
        RoomHandler.getInstance().addRoom(this);
        httpClient = HttpClients.createDefault();
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setAutoNext(boolean value) {
        autoNext = value;
    }

    public void pause(JsonNumber current, Session session, boolean intended) {
        if (this.playing) {
            buffering = !intended;
            this.playing = false;
            this.timestamp = current;
            JsonProvider provider = JsonProvider.provider();
            JsonObject messageJson = provider.createObjectBuilder()
                    .add("action", "pause")
                    .add("current", current)
                    .build();
            if (!intended) {
                this.markReady(session, false);
            }
            UserSessionHandler.getInstance().sendToRoom(messageJson, this);
        }
    }

    public boolean isHost(Session s) {
        return s == host;
    }

    public List<Session> getSessions() {
        return new LinkedList<Session>(sessions);
    }

    public void addSession(Session session, String name) {
        setName(session, name);
        readyStates.put(session, false);
        sessions.add(session);
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "roomID")
                .add("id", id)
                .build();
        UserSessionHandler.getInstance().sendToSession(session, messageJson);
        if (!video.equals("")) {
            sendVideoToSession(session, true);
        }
        sendRoomList();
    }

    public void removeSession(Session session) {
        readyStates.remove(session);
        nameMap.remove(session);
        sessions.remove(session);
        if (session == host) {
            if (sessions.isEmpty()) {
                RoomHandler.getInstance().removeRoom(this);
                return;
            } else {
                host = sessions.get(0);
                JsonProvider provider = JsonProvider.provider();
                JsonObject messageJson = provider.createObjectBuilder()
                        .add("action", "owner")
                        .build();
                UserSessionHandler.getInstance().sendToSession(host, messageJson);
            }
        }
        sendRoomList();
    }

    private void sendRoomList() {
        StringBuilder builder = new StringBuilder();
        builder.append("<ul class=\"mdc-list mdc-list--dense\">");
        for (Session s : sessions) {
            builder.append("<li class=\"mdc-list-item\">");
            builder.append(nameMap.get(s));
            if (s == host) {
                builder.append("&nbsp;<img class=\"mdc-list-item__start-detail\" src=\"res/crown.svg\" alt=\"Crown\" height=\"15\" width=\"15\"> ");
            }
            builder.append("</li>");
        }
        builder.append("</ul>");
        String roomString = builder.toString();
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "room-list")
                .add("roomString", roomString)
                .build();
        UserSessionHandler.getInstance().sendToRoom(messageJson, this);
    }

    public void changeName(Session s, String name) {
        String old = nameMap.get(s);
        if (!name.equals(old)) {
            setName(s, name);
            sendRoomList();
        }
    }

    private void setName(Session s, String name) {
        if (name.contains("<")) {
            name = "User " + random.nextInt(10000);
        }
        nameMap.put(s, name);
    }

    public void setVideo(String url) {
        timestamp = null;
        for (Session s : readyStates.keySet()) {
            markReady(s, false);
        }
        video = url;
        playing = false;
        isDirectLink = checkDirectLink(url);
        sendVideoToRoom();
    }

    private void sendVideoToRoom() {
        for (Session s : sessions) {
            sendVideoToSession(s, false);
        }
    }

    public void setCurrent(JsonNumber current) {
        this.timestamp = current;
    }

    private void sendVideoToSession(Session s, boolean newJoin) {
        if (newJoin) {
            if (timestamp != null) {
                System.out.println("pause");
                pause(timestamp, s, false);
            }
        }
        String url = createDirectLink();
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson;
        if (timestamp == null) {
            messageJson = provider.createObjectBuilder()
                    .add("action", "video")
                    .add("url", url)
                    .add("current", 0)
                    .build();
        } else {
            messageJson = provider.createObjectBuilder()
                    .add("action", "video")
                    .add("url", url)
                    .add("current", timestamp)
                    .build();
        }
        UserSessionHandler.getInstance().sendToSession(s, messageJson);
    }

    private String createDirectLink() {
        System.out.println(video);
        if (isDirectLink) {
            return video;
        }
        //String website = ripLink;
        String website = "";
        try {
            URL url = new URL(video);
            if (url.getHost().equals("proxer.me")) {
                website = getProxerLink();
                if (website == null || website.equals("")) {
                    sendDebugToHost("Couldn't find video URL. May be my fault or your fault");
                }
            } else if (url.getHost().equals("9anime.to")) {
                episode = 0;
                _9animeLink = video;
                System.out.println("getting 9anime link");
                website = get9animeLink();
            } else {
                sendDebugToHost("Host not supported (yet?)");
            }
        } catch (MalformedURLException e) {
            sendDebugToHost("invalid URL");
        }
        video = website;
        isDirectLink = true;
        return website;
    }

    private String get9animeLink() {
        //String content = getWebsiteContent(video, "");
        if(_9animeLink.equals("")) {
            return "";
        }
        if(episode == 0) {
            System.out.println("Episode is 0. creating episode element from url " + video);
            _9animeLink = video;
            anime = new Anime(video);
            System.out.println(anime.getAnimeSearchObject().getLink());
            Episode episode = null;
            try {
                episode = anime.getEpisodeObject(video);
            } catch (No9AnimeUrlException e) {
                e.printStackTrace();
            }
            if(episode == null) {
                System.out.println("ERROR: episode element is null");
                return "";
            }
            this.episode = episode.getEpNumInt();
            return episode.getEpisodeUrl();
        } else {
            Episode episode = anime.getEpisodeObject(_9animeLink, this.episode);
            if(episode != null) {
                return episode.getEpisodeUrl();
            } else {
                return "";
            }
        }
    }

    public int getEpisodeNumber() {
        return episode;
    }

    private String getWebsiteContent(String url, String cookie) {
        /*String website = "http://vjs.zencdn.net/v/oceans.mp4";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        if(url.contains("proxer")) {
            if(url.contains("https")) {
                url = url.replaceFirst("https","http");
                httpget = new HttpGet(url);
            }
            httpget.setHeader("User-Agent","Mozilla/5.0 (Unknown; Linux x86_64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
            httpget.setHeader("X-Requested-With", "XMLHttpRequest");
            httpget.setHeader("Origin","http://proxer.me");
        }
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                website =  EntityUtils.toString(entity, "UTF-8");
            } catch(NullPointerException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        String website = "";
        if (url.contains("proxer") && !UserSessionHandler.getInstance().proxRequest()) {
            sendDebugToHost("Too many Proxer requests");
            return ripLink;
        }
        try {
            System.out.println("kek");
            StringBuilder result = new StringBuilder();
            URL urlT = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlT.openConnection();
            conn.setRequestMethod("GET");
            if (cookie != null && !cookie.equals("")) {
                if (url.contains("proxer")) {
                    conn.addRequestProperty("Cookie", "chatactivate=false; _pk_ref.1.0e5d=%5B%22%22%2C%22%22%2C1494012577%2C%22https%3A%2F%2Fwww.google.de%2F%22%5D; wiki_db_wiki_UserID=67335; wiki_db_wiki_UserName=Schoki-; __cfduid=d5051858a3acb8b0e349ee61defdc30551498418460; cookieconsent_dismissed=yes; stream_choose=mp4upload; joomla_remember_me_d125cc75d135b0170a7c24322ab2c4c5=y3BrUANeyZGxJfac.jh3fkcL2pDiXrljRs1gK; proxer_loggedin=true; style=gray; default_design=gray; tmode=ht; MW57ac91865e5064f231cf620988223590=U2Nob2tpLXw2Y2RmOTYxODZmNjNkODFlMjYzN2JkMDEyNTZlNDQ5OQ%3D%3D; e0da4f913f5f05ed7a3f6dc5f0488c7b=n8vucfkmr2o3bv995bphgdo3n3; joomla_user_state=logged_in");
                } else {
                    conn.addRequestProperty("Cookie", cookie);
                }
            }
            conn.addRequestProperty("User-Agent", "Mozilla/4.76");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            lastCookie = conn.getHeaderField("Set-Cookie");
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            website = result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            sendDebugToHost("Something went wrong");
        }
        return website;
    }

    private String getProxerLink() {
        String content = getWebsiteContent(video, "");
        if (content.equals(ripLink)) {
            return ripLink;
        }
        Pattern STREAM_PATTERN = Pattern.compile("(var streams = ?)(\\[.*?\\]);");
        Matcher m = STREAM_PATTERN.matcher(content);
        String res = "";
        String streamString = "";
        try {
            while (m.find()) {
                streamString = m.group(2);
            }
            Gson gson = new Gson();
            Stream[] streams = gson.fromJson(streamString, Stream[].class);
            res = parseMp4(streams);
        } catch (IndexOutOfBoundsException e) {
            sendDebugToHost("Couldn't parse Proxer stream link");
        } catch (NullPointerException e) {
            sendDebugToHost("Couldn't parse Proxer stream link");
        }
        return res;
    }

    private String parseMp4(Stream[] streams) {
        String res = "";
        Stream stream = null;
        for (Stream s : streams) {
            if (s.type.equals("mp4upload")) {
                stream = s;
            }
        }
        if (stream != null) {
            stream.replace = stream.replace.replace("#", stream.code);
            String content = getWebsiteContent("https:" + stream.replace, lastCookie);
            Pattern MP4_PATTERN = Pattern.compile("\\|var\\|com\\|(.*?)\\|url");
            //System.out.println(content);
            Matcher m = MP4_PATTERN.matcher(content);
            if (m.find()) {
                String raw = m.group(1);
                String[] parts = raw.split("\\|");
                String token = "";
                for (String s : parts) {
                    if (s.length() >= 25) {
                        token = s;
                    }
                }
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(parts[1]);
                urlBuilder.append("://");
                urlBuilder.append(parts[4]);
                urlBuilder.append(".mp4upload.com:282/d/");
                urlBuilder.append(token);
                urlBuilder.append("/video.mp4");
                res = urlBuilder.toString();
            }
        }
        return res;
    }

    private String parseProxer(Stream[] streams) {
        Stream s = null;
        for (Stream stream : streams) {
            if (stream.type.equals("proxer-stream")) {
                s = stream;
            }
        }
        if (s == null) {
            return "";
        }
        s.replace = s.replace.replace("#", s.code);
        String iframe = getWebsiteContent("https:" + s.replace + "?utype=member", "");
        Document doc = Jsoup.parse(iframe);
        Element vid = doc.select("video").first();
        Pattern VIDEO_LINK_PATTERN = Pattern.compile("src=\"(.*?)\"");
        Matcher m = VIDEO_LINK_PATTERN.matcher(vid.html());
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private boolean checkDirectLink(String url) {
        boolean res = false;
        HttpHead head = new HttpHead(url);
        try {
            CloseableHttpResponse response = httpClient.execute(head);
            try {
                String contentType = response.getFirstHeader("Content-Type").getValue();
                if (contentType != null && contentType.contains("video")) {
                    res = true;
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendDebugToHost("Something went wrong. Either the server RIP'd or your URL is shit");
        }
        return res;
    }

    private void sendDebugToHost(String s) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "debug")
                .add("message", s)
                .build();
        UserSessionHandler.getInstance().sendToSession(host, messageJson);
    }

    public void videoFinished() {
        playing = false;
        buffering = false;
        loadNextVideo();
    }

    public void reset9anime() {
        episode = 0;
        anime = null;
        _9animeLink = "";
    }

    public void loadNextVideo() {
        timestamp = null;
        if(playlist.isEmpty() && autoNext) {
            episode++;
            String newUrl = get9animeLink();
            if(!"".equals(newUrl)) {
                setVideo(newUrl);
            } else {
                sendDebugToHost("failed to load next Video");
            }
        } else if(!playlist.isEmpty()) {
            //TODO play playlist
        }
    }

    public void markReady(Session s, boolean status) {
        readyStates.put(s, status);
        if (!playing && buffering) {
            play();
        }
    }

    public void play() {
        boolean flag = true;
        for (Session s : sessions) {
            if (!readyStates.get(s)) {
                sendBufferedRequest(s);
                flag = false;
            }
        }
        if (flag) {
            //start playing
            JsonProvider provider = JsonProvider.provider();
            JsonObject messageJson = provider.createObjectBuilder()
                    .add("action", "play")
                    .build();
            playing = true;
            buffering = false;
            UserSessionHandler.getInstance().sendToRoom(messageJson, this);
        }
    }

    private void sendBufferedRequest(Session s) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "bufferedRequest")
                .build();
        UserSessionHandler.getInstance().sendToSession(s, messageJson);
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return video;
    }

    public Anime getAnime() {
        return this.anime;
    }
}
