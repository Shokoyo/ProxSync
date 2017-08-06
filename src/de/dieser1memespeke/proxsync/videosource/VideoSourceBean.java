package de.dieser1memespeke.proxsync.videosource;


import javax.ejb.Stateless;

@Stateless
public class VideoSourceBean {

    public String buildPlayer(String url) {
        return "<link href=\"http://vjs.zencdn.net/6.2.4/video-js.css\" rel=\"stylesheet\">\n" +
                " \n" +
                "  <!-- If you'd like to support IE8 -->\n" +
                "  <script src=\"http://vjs.zencdn.net/ie8/1.1.2/videojs-ie8.min.js\"></script>\n" +
                "  <video id=\"my-video\" class=\"video-js\" controls preload=\"auto\" width=\"640\" height=\"264\"\n" +
                "  poster=\"" + "" + "\" data-setup=\"{}\">\n" +
                "    <source src=\"" + url + "\" type='video/mp4'>\n" +
                "    <p class=\"vjs-no-js\">\n" +
                "      To view this video please enable JavaScript, and consider upgrading to a web browser that\n" +
                "      <a href=\"http://videojs.com/html5-video-support/\" target=\"_blank\">supports HTML5 video</a>\n" +
                "    </p>\n" +
                "  </video>\n" +
                " \n" +
                "  <script src=\"http://vjs.zencdn.net/6.2.4/video.js\"></script>";
    }
}