package de.dieser1memesprech.proxsync.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Jeremias on 19.09.2017.
 */
public class LoginUtil {
    public static String getUid(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("loginData")) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
