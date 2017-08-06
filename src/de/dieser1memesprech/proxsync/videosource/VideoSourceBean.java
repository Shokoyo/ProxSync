package de.dieser1memesprech.proxsync.videosource;


import javax.ejb.Stateless;

@Stateless
public class VideoSourceBean {

    public String buildPlayer(String url) {
        return  "<video id=\"my-player\" class=\"video-js\" controls preload=\"auto\" width=\"640\" height=\"264\">\n" +
                "<source src=\"" + url + "\" type=\"video/mp4\">\n" +
                "</video>\n" +
                "<script src=\"res/player-script.js\"></script>";
    }
}