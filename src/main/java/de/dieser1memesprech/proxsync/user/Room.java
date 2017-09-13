package de.dieser1memesprech.proxsync.user;

import com.google.gson.Gson;
import de.dieser1memesprech.proxsync.websocket.UserSessionHandler;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Room {
    private static final String ripLink = "http://i.imgur.com/eKmmyv1.mp4";
    private HashMap<Session, Boolean> readyStates = new HashMap<Session, Boolean>();
    private List<Session> sessions;
    private String video = "";
    private Session host;
    private int id;
    private boolean playing = false;
    private boolean buffering = false;
    private boolean isDirectLink = false;
    private CloseableHttpClient httpClient;
    private JsonNumber timestamp;

    public Room(Session host) {
        this.host = host;
        Random random = new Random();
        do {
            id = random.nextInt(999);
        } while (!RoomHandler.getInstance().checkId(id));
        sessions = new LinkedList<Session>();
        this.addSession(host);
        RoomHandler.getInstance().addRoom(this);
        httpClient = HttpClients.createDefault();
    }

    public boolean isPlaying() {
        return playing;
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
            if(!intended) {
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

    public void addSession(Session session) {
        readyStates.put(session, false);
        sessions.add(session);
        if (!video.equals("")) {
            sendVideoToSession(session, true);
        }
    }

    public void removeSession(Session session) {
        readyStates.remove(session);
        sessions.remove(session);
    }

    public void setVideo(String url) {
        for(Session s : readyStates.keySet()) {
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
        if(newJoin) {
            if(timestamp != null) {
                System.out.println("pause");
                pause(timestamp, s, false);
            }
        }
        String url = createDirectLink();
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson;
        if(timestamp == null) {
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
        if (isDirectLink) {
            return video;
        }
        String website = "";
        try {
            URL url = new URL(video);
            if(url.getHost().equals("proxer.me")) {
                website = getProxerLink();
                if(website == null || website.equals("")) {
                    sendDebugToHost("Couldn't find video URL. May be my fault or your fault");
                }
            } else {
                String content = getWebsiteContent(video);
                System.out.println(content);
                sendDebugToHost("Host not supported (yet?)");
            }
        } catch(MalformedURLException e) {
            sendDebugToHost("invalid URL");
        }
        video = website;
        isDirectLink = true;
        return website;
    }

    private String getWebsiteContent(String url) {
        String website = "http://vjs.zencdn.net/v/oceans.mp4";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
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
        }
        return website;
    }

    private String getProxerLink() {
        if(!UserSessionHandler.getInstance().proxRequest()) {
            sendDebugToHost("Too many Proxer requests");
            return ripLink;
        }
        String content = getWebsiteContent(video);
        //System.out.println(content);
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
        } catch(IndexOutOfBoundsException e) {
            sendDebugToHost("Couldn't parse Proxer stream link");
        } catch(NullPointerException e) {
            sendDebugToHost("Couldn't parse Proxer stream link");
        }
        return res;
    }

    private String parseMp4(Stream[] streams) {
        String res = "";
        Stream stream = null;
        for(Stream s: streams) {
            if(s.type.equals("mp4upload")) {
                stream = s;
            }
        }
        if(stream != null) {
            stream.replace = stream.replace.replace("#",stream.code);
            String content = getWebsiteContent("https:" + stream.replace);
            Pattern MP4_PATTERN = Pattern.compile("\\|var\\|com\\|(.*?)\\|url");
            //System.out.println(content);
            Matcher m = MP4_PATTERN.matcher(content);
            if(m.find()) {
                String raw = m.group(1);
                String[] parts = raw.split("\\|");
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(parts[1]);
                urlBuilder.append("://");
                urlBuilder.append(parts[4]);
                urlBuilder.append(".mp4upload.com:");
                urlBuilder.append(parts[17]);
                urlBuilder.append("/d/");
                urlBuilder.append(parts[16]);
                urlBuilder.append("/video.mp4");
                res = urlBuilder.toString();
            }
        }
        return res;
    }

    private String parseProxer(Stream[] streams) {
        Stream s = null;
        for(Stream stream : streams) {
            if(stream.type.equals("proxer-stream")) {
                s = stream;
            }
        }
        if(s == null) {
            return "";
        }
        s.replace = s.replace.replace("#", s.code);
        String iframe = getWebsiteContent("https:" + s.replace + "?utype=member");
        Document doc = Jsoup.parse(iframe);
        Element vid = doc.select("video").first();
        Pattern VIDEO_LINK_PATTERN = Pattern.compile("src=\"(.*?)\"");
        Matcher m = VIDEO_LINK_PATTERN.matcher(vid.html());
        if(m.find()) {
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

    public void markReady(Session s, boolean status) {
        readyStates.put(s, status);
        if(!playing && buffering) {
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
        UserSessionHandler.getInstance().sendToSession(s,messageJson);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return video;
    }
}
