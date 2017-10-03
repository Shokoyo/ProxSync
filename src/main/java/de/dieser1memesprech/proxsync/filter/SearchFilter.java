package de.dieser1memesprech.proxsync.filter;

import de.dieser1memesprech.proxsync._9animescraper.AnimeSearchObject;
import de.dieser1memesprech.proxsync._9animescraper.util.AnimeUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeremias on 03.10.2017.
 */
public class SearchFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            if(httpRequest.getMethod().equals("GET")) {
                for(Map.Entry<String, String[]> entry : httpRequest.getParameterMap().entrySet()) {
                    if(entry.getKey().equals("search")) {
                        List<AnimeSearchObject> searchList = AnimeUtils.search(entry.getValue()[0]);
                        servletResponse.setContentType("application/json");

                    }
                }
            }
        } catch(ClassCastException | ArrayIndexOutOfBoundsException e) {

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
