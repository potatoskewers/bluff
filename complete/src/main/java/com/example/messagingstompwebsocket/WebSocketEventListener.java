package com.example.messagingstompwebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        System.out.println("ID Descriptions: " + sessionId);
        String UUID = UUIDs.getUserIDs().get(sessionId);
        UUIDs.getUUIDs().remove(UUID);
        System.out.println("removing UUID" + UUID);
        // Log or handle the disconnection event
        logger.info("Client disconnected: Session ID = " + sessionId + "total players: " + UUIDs.getUUIDs().size() + UUIDs.getUUIDs() + " " + UUIDs.getUserIDs());

        // Perform any cleanup or notification logic here
        // e.g., remove user from game, notify other clients, etc.
    }
}
