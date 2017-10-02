package de.dieser1memesprech.proxsync.database;

import java.util.List;

/**
 * Created by Jeremias on 29.09.2017.
 */
public class AiringEntry implements Comparable<AiringEntry> {
    private long id;
    private long duration;
    private String title;
    private String format;
    private String startDate;
    private String source;
    private List<String> genres;
    private long episodes;
    private String poster;
    private long score;
    private long popularity;
    private String youtubeId;
    private String description;
    private String studioName;

    public AiringEntry() {
    }

    public String getGenresOverflow() {
        if(genres == null) {
            return "";
        }
        if(genres.size()<=3) {
            return String.join(",", genres);
        } else {
            String res = String.join(",", genres.subList(0,3));
            res += ",<span title=\""
                    + String.join(",", genres.subList(3, genres.size()))
                    + "\">...</span>";
            return res;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public long getEpisodes() {
        return episodes;
    }

    public void setEpisodes(long episodes) {
        this.episodes = episodes;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getPopularity() {
        return popularity;
    }

    public void setPopularity(long popularity) {
        this.popularity = popularity;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudioName() {
        return studioName;
    }

    public void setStudioName(String studioName) {
        this.studioName = studioName;
    }

    @Override
    public int compareTo(AiringEntry o) {
        return Long.compare(this.popularity, o.getPopularity());
    }
}
