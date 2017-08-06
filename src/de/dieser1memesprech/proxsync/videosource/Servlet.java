package de.dieser1memesprech.proxsync.videosource;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Jeremias on 06.08.2017.
 */
@WebServlet(name = "proxSync")
public class Servlet extends HttpServlet {
    @EJB
    private VideoSourceBean videoSourceBean;

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getParameter("url");
        String player = videoSourceBean.buildPlayer(url);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<link href=\"http://vjs.zencdn.net/6.2.4/video-js.css\" rel=\"stylesheet\">");
            out.println("<script src=\"http://vjs.zencdn.net/6.2.4/video.js\"></script>");
            out.println("<title>Prox-Sync</title>");
            out.println("</head>");
            out.println("<body>");
            out.println(player);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
