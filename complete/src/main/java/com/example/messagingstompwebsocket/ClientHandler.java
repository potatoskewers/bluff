package com.example.messagingstompwebsocket;

import com.sun.security.auth.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
public class ClientHandler extends DefaultHandshakeHandler {
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    String sessionID;
    static String randomID;
    final Object lock = new Object();

    public void setRandomID(String randomID) {
        this.randomID = randomID;
    }
    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        System.out.println("logging session connect event");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionID = headerAccessor.getSessionId();
        System.out.println("sessionID: " + sessionID);
        // Store the session ID in a shared map or other storage if needed
        // Ensure that this map can be accessed by the `determineUser()` method later
        System.out.println("adding sessionID to the hashmap: " + sessionID);
        UUIDs.getUserIDs().put(sessionID, randomID);
    }
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String randomId = UUID.randomUUID().toString();
        String sessionID = request.getHeaders().getFirst("simpSessionId");
        setRandomID(randomId);

//        System.out.println(request.getLocalAddress().toString() + " " + request.getRemoteAddress().toString() + " " + request.getURI().toString() + " " + request.getHeaders().toString());
//        UUIDs.getUserIDs().put(randomId, )
        UUIDs.getUUIDs().add(randomId);
        logger.info("User with ID {}joined", randomId);
        System.out.println("user with ID  +" + randomId + "joined");
        System.out.println(UUIDs.getUserIDs());
        //declare the userID in message.

        return new UserPrincipal(randomId);
    }

}