package de.dieser1memesprech.proxsync.user;

/**
 * Created by Jeremias on 15.09.2017.
 */
public class Video {
    String animeTitle;
    String url;
    String episodeTitle;
    String episodePoster;
    boolean infoGot;
    int episode;
    String episodeCount;
    public String key;
    public int getEpisode() {
        return episode;
    }


    public Video(String url, int episode) {
        this.url = url;
        this.episode = episode;
        this.infoGot = false;
        this.animeTitle = url;
        this.url = url;
        this.episodeTitle = "";
        this.episodeCount = "";
        this.episodePoster = "https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/ic_ondemand_video_black_24px.svg?alt=media&token=fb90a1ff-ef22-4f7a-a900-48363ff27241";
    }

    public Video(String url) {
        this.infoGot = false;
        this.animeTitle = url;
        this.url = url;
        this.episodeTitle = "";
        this.episodePoster = "https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/ic_ondemand_video_black_24px.svg?alt=media&token=fb90a1ff-ef22-4f7a-a900-48363ff27241";
        this.episode = 0;
        this.episodeCount = "";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public String getEpisodePoster() {
        return episodePoster;
    }

    public String getEpisodeCount() { return episodeCount; }

    public void setEpisodePoster(String episodePoster) {
        this.episodePoster = episodePoster;
    }

    public String getKey() {
        return key;
    }
}
