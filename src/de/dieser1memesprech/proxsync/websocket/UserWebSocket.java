package de.dieser1memesprech.proxsync.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by Jeremias on 06.08.2017.
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class UserWebSocket {
    @OnOpen
    public void open(Session session) {

    }

    @OnClose
    public void close(Session session) {

    }

    @OnError
    public void onError(Throwable error) {

    }

    @OnMessage
    public void handleMessage(String message, Session session) {

    }
}
