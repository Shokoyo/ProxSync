package de.dieser1memesprech.proxsync.filters;

/**
 * Created by Jeremias on 11.09.2017.
 */
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class RoomFilter implements Filter {
    private FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletRequest.getParameter("r") != null) {
            servletResponse.setContentType("text/html");
            Writer w = servletResponse.getWriter();
            w.write(PageCreator.getRoomRedirector(servletRequest.getParameter("r"), (HttpServletRequest) servletRequest));
            w.flush();
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {

    }
}