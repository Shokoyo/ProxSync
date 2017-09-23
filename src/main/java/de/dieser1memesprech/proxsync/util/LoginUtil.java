package de.dieser1memesprech.proxsync.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class LoginUtil {
    public static String getUid(HttpServletRequest request) {
        boolean anonymous = true;
        String res = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("anonymous")) {
                    anonymous = Boolean.parseBoolean(cookie.getValue());
                }
                if (cookie.getName().equals("loginData")) {
                    res = cookie.getValue();
                }
            }
        }
        if(!anonymous) {
            return res;
        } else {
            return "";
        }
    }
}
