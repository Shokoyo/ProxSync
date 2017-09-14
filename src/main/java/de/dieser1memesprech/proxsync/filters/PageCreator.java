package de.dieser1memesprech.proxsync.filters;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.json.JsonNumber;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Jeremias on 11.09.2017.
 */
class PageCreator {
    static String getDefaultPage(HttpServletRequest request) {
        String res = "";
        try {
            ServletContext servletContext = request.getSession().getServletContext();
            File input = new File(servletContext.getRealPath("index.html"));
            Document doc = Jsoup.parse(input, "UTF-8", "");
            res = doc.toString();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    static String getRoomRedirector(String id, HttpServletRequest request) {
        String s = getDefaultPage(request);
        Document doc = Jsoup.parse(s);
        Element body = doc.body();
        body
                .appendElement("script")
                .attr("type","text/javascript")
                .appendChild(new DataNode(getJavaScript(id),""));
        return doc.toString();
    }
    static String getJavaScript(String id) {
        return  "socket.onopen = joinRoomNew;\n" +
                "function joinRoomNew() {" +
                "isOwner = false;\n" +
                "        var userAction = {\n" +
                "            action: \"join\",\n" +
                "            name: getCookie(\"username\"),\n" +
                "            id: \"" + id + "\"\n" +
                "        };\n" +
                "        socket.send(JSON.stringify(userAction));" +
                "}";
    }
}
