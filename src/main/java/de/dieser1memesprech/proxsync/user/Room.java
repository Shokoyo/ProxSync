package de.dieser1memesprech.proxsync.user;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.dieser1memesprech.proxsync._9animescraper.Anime;
import de.dieser1memesprech.proxsync._9animescraper.Episode;
import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import de.dieser1memesprech.proxsync._9animescraper.util.HtmlUtils;
import de.dieser1memesprech.proxsync.database.Database;
import de.dieser1memesprech.proxsync.util.NamespaceContextMap;
import de.dieser1memesprech.proxsync.util.RandomString;
import de.dieser1memesprech.proxsync.websocket.UserSessionHandler;
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.model.FirebaseResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.InputSource;

import javax.json.*;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Room {
    private int episode;

    public LinkedList<Video> getPlaylist() {
        return playlist;
    }

    private LinkedList<Video> playlist;
    private static final String USER_AGENT = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13";
    private static final String ripLink = "http://i.imgur.com/eKmmyv1.mp4";
    private HashMap<Session, Boolean> readyStates = new HashMap<Session, Boolean>();
    private HashMap<Session, User> userMap = new HashMap<Session, User>();
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
    private Video lastVideo;
    private static RandomString randomString = new RandomString(10);

    public Room(Session host, String hostname, String hostuid, boolean hostanonymous) {
        playlist = new LinkedList<Video>();
        this.host = host;
        do {
            id = randomString.nextString();
        } while (!RoomHandler.getInstance().checkId(id));
        sessions = new LinkedList<Session>();
        this.addSession(host, hostname, hostuid, hostanonymous);
        RoomHandler.getInstance().addRoom(this);
        httpClient = HttpClients.createDefault();
    }

    public Video getLastVideo() {return lastVideo;}

    public void setId(String id) {
        this.id = id;
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

    public void addSession(Session session, String name, String uid, boolean anonymous) {
        String avatarUrl = Database.getAvatarFromDatabase(uid);
        if("null".equals(avatarUrl)) {
            avatarUrl = "https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/panda.svg?alt=media&token=6f4d5bf1-af69-4211-994d-66655456d91a";
        }
        User user = new User(uid, name, avatarUrl, anonymous);
        userMap.put(session, user);
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
            sendPlaylist();
        }
        sendRoomList();
    }

    public void removeSession(Session session) {
        readyStates.remove(session);
        userMap.remove(session);
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

    public void addVideo(String url) {
        playlist.add(new Video(url));
        if (playlist.size() == 1) {
            updatePlaylistInfo(playlist.peek());
            if (!playlist.isEmpty()) {
                System.out.println(playlist.peek().episode);
                setVideo(playlist.peek().url, playlist.peek().episode);
            }
        }
        sendPlaylist();
    }

    public void addVideo(String url, int episode) {
        System.out.println(episode);
        playlist.add(new Video(url, episode));
        this.episode = episode;
        if (playlist.size() == 1) {
            updatePlaylistInfo(playlist.peek());
            if (!playlist.isEmpty()) {
                setVideo(playlist.peek().url, playlist.peek().episode);
            }
        }
        sendPlaylist();
    }

    private void sendPlaylist() {
        updatePlaylistInfo();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Video v : playlist) {
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("title", v.animeTitle)
                    .add("episodeTitle", v.episodeTitle)
                    .add("episodePoster", v.episodePoster)
                    .add("episode", v.episode)
                    .add("episodeCount", v.episodeCount)
                    .build());
        }
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "playlist")
                .add("playlist", arrayBuilder.build())
                .build();
        UserSessionHandler.getInstance().sendToRoom(messageJson, this);
    }

    private void updatePlaylistInfo() {
        for (Video v : playlist) {
            updatePlaylistInfo(v);
        }
    }

    public void playNow(int episode) {
        if (episode > 0) {
            for (int i = 0; i < episode - 1; i++) {
                playlist.poll();
            }
            loadNextVideo();
        }
    }

    public void delete(int episode) {
        if (episode == 0) {
            if (playlist.size() != 1) {
                loadNextVideo();
            }
        } else {
            playlist.remove(playlist.get(episode));
            sendPlaylist();
        }
    }

    private void updatePlaylistInfo(Video v) {
        if (!v.infoGot) {
            System.out.println("fetching info for video with url " + v.url);
            isDirectLink = checkDirectLink(v.url);
            v.url = createDirectLink(v.url);
            if (v.url.equals("")) {
                playlist.remove(v);
                sendDebugToHost("invalid URL");
            } else if (anime != null) {
                v.animeTitle = anime.getTitle();
                v.episodeCount = anime.getEpisodeCount();
                String link = anime.getAnimeSearchObject().getLink();
                v.key = link.substring(link.lastIndexOf("/") + 1);
                if(v.episode == 0) {
                    v.episode = episode;
                }
                v.episodePoster = anime.getAnimeSearchObject().getPoster();
                System.out.println("updating info for " + v.animeTitle + ":" + anime.getAnimeSearchObject().getLastEpisode()
                + "," + anime.getAnimeSearchObject().getEpisodeCount() + "," + v.episodePoster);
                Database.updateAnimeInfo(v.key, anime.getAnimeSearchObject().getLastEpisode() + "",
                        anime.getAnimeSearchObject().getEpisodeCount() + "", v.episodePoster);
                String epTitle = Database.getEpisodeTitleFromDatabase(v.key, episode);
                if (epTitle == null || epTitle.equals("null")) {
                    int episodeCount;
                    try {
                        episodeCount = Integer.parseInt(anime.getAnimeSearchObject().getLastEpisode());
                        v.episodeTitle = getEpisodeTitle(v.key, v.animeTitle, v.episode, episodeCount);
                    } catch(NumberFormatException e) {
                        v.episodeTitle = "";
                        Database.addAnimeinfoToDatabase(v.key, v.animeTitle, new ArrayList<>());
                        System.out.println("Couldn't parse episode count");
                    }
                } else {
                    v.episodeTitle = epTitle;
                }
            }
            v.infoGot = true;
        }
    }

    private String evaluateXPath(String xml, String expr) {
        String res = "";
        try {
            NamespaceContext nsContext = new NamespaceContextMap(
                    "xml", "http://www.w3.org/XML/1998/namespace");
            XPath xPath = XPathFactory.newInstance().newXPath();
            xPath.setNamespaceContext(nsContext);
            return xPath.evaluate(expr, new InputSource(new StringReader(xml)));
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String getEpisodeTitle(String key, String animeTitle, int episode, int episodeCount) {
        System.out.println("Getting titles for " + animeTitle);
        String res = "";
        List<String> episodeNames = new ArrayList<String>();
            System.out.println("http://anisearch.outrance.pl/?task=search&query=\\"
                    + animeTitle);
            String content = HtmlUtils.getHtmlContent("http://anisearch.outrance.pl/?task=search&query=\\"
                    + animeTitle);
            System.out.println(content);
            String aid = evaluateXPath(content, "//anime/@aid");
            System.out.println("Anime ID: " + aid);
            if(aid != "") {
                content = HtmlUtils.getHtmlContent("http://api.anidb.net:9001/httpapi?request=anime&" +
                        "client=anisync&clientver=1&protover=1&aid=" + aid);
                for (int i = 1; i <= episodeCount; i++) {
                    episodeNames.add(evaluateXPath(content, "//episode[epno=\"" + i + "\"]/title[@xml:lang=\"en\"]/text()"));
                }
                res = evaluateXPath(content, "//episode[epno=\"" + episode + "\"]/title[@xml:lang=\"en\"]/text()");
                System.out.println(res);
            }
        Database.addAnimeinfoToDatabase(key, animeTitle, episodeNames);
        return res;
    }

    private void sendRoomList() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Session s : sessions) {
            arrayBuilder.add(Json.createObjectBuilder()
                    .add("uid", userMap.get(s).getUid())
                    .add("name", userMap.get(s).getName())
                    .add("avatar", userMap.get(s).getAvatarUrl())
                    .add("isOwner", s == host)
                    .build());
        }
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", "room-list")
                .add("userList", arrayBuilder.build())
                .build();
        UserSessionHandler.getInstance().sendToRoom(messageJson, this);
    }

    public void changeName(Session s, String name) {
        String old = userMap.get(s).getName();
        if (!name.equals(old)) {
            setName(s, name);
            sendRoomList();
        }
    }

    private void setName(Session s, String name) {
        if (name.contains("<")) {
            name = "User " + random.nextInt(10000);
        }
        userMap.get(s).setName(name);
    }

    public void setVideo(String url, int episode) {
        this.episode = episode;
        timestamp = null;
        for (Session s : readyStates.keySet()) {
            markReady(s, false);
        }
        video = url;
        playing = false;
        sendVideoToRoom();
        if (anime != null) {
            JsonProvider provider = JsonProvider.provider();
            JsonObject messageJson = provider.createObjectBuilder()
                    .add("action", "animeInfo")
                    .add("title", getAnime().getTitle())
                    .add("episode", getEpisodeNumber())
                    .add("episodeCount", getAnime().getEpisodeCount())
                    .build();
            UserSessionHandler.getInstance().sendToRoom(messageJson, this);
        }
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
        String url = createDirectLink(playlist.peek().url);
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

    private String createDirectLink(String video) {
        System.out.println(video);
        if (isDirectLink) {
            return video;
        }
        //String website = ripLink;
        String website = "";
        try {
            URL url = new URL(video);
            if (url.getHost().equals("proxer.me")) {
                website = getProxerLink(video);
                if (website == null || website.equals("")) {
                    sendDebugToHost("Couldn't find video URL. May be my fault or your fault");
                }
            } else if (url.getHost().equals(Configuration.instance.SITE_NAME)) {
                _9animeLink = video;
                System.out.println("getting 9anime link");
                website = get9animeLink(video);
            } else {
                sendDebugToHost("Host not supported (yet?)");
            }
        } catch (MalformedURLException e) {
            sendDebugToHost("invalid URL");
        }
        isDirectLink = true;
        return website;
    }

    private String get9animeLink(String video) {
        //String content = getWebsiteContent(video, "");
        if (_9animeLink.equals("")) {
            return "";
        }
        if (episode == 0) {
            System.out.println("Episode is 0. creating episode element from url " + video);
            _9animeLink = video;
            anime = new Anime(video);
            System.out.println(anime.getAnimeSearchObject().getLink());
            Episode episode = null;
            episode = anime.getEpisodeList().get(0);
            if (episode == null) {
                System.out.println("ERROR: episode element is null");
                return "";
            }
            this.episode = anime.getEpisodeList().indexOf(episode);
            return episode.getSourceUrl();
        } else {
            if(anime == null) {
                anime = new Anime(video);
            }
            if(anime.getEpisodeList().isEmpty()) {
                return "";
            }
            try {
                Episode episode = anime.getEpisodeList().get(this.episode - 1);
                if (episode != null) {
                    String episodeSource = episode.getSourceUrl();
                    return episodeSource;
                } else {
                    return "";
                }
            } catch(IndexOutOfBoundsException e) {
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

    private String getProxerLink(String video) {
        String content = HtmlUtils.getHtmlContent(video);
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
        if(url.contains("proxer.me/watch")) {
            return false;
        }
        if(url.contains("youtube") || url.contains("youtu.be")) {
            return true;
        } else if(url.contains("mp4upload") && url.contains("video.mp4")) {
            return true;
        }
        HttpHead head = new HttpHead(url);
        try {
            CloseableHttpResponse response = httpClient.execute(head);
            try {
                String contentType = response.getFirstHeader("Content-Type").getValue();
                System.out.println(contentType);
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
        Video v = playlist.poll();
        lastVideo = v;
        timestamp = null;
        if (playlist.isEmpty() && autoNext && !_9animeLink.equals("")) {
            episode++;
            if (v != null) {
                addVideo(get9animeLink(_9animeLink));
            }
        } else if (!playlist.isEmpty()) {
            sendPlaylist();
            setVideo(playlist.peek().getUrl(), playlist.peek().episode);
        } else {
            sendPlaylist();
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
                buffering = true;
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

    public Map<Session, User> getUserMap() {
        return userMap;
    }
}
