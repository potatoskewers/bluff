package com.example.messagingstompwebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class MessagingService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendPrivateMessage(String username, String message) {
        String destination = "/user/" + username + "/topic/private-message"; // Specify the user's destination
        messagingTemplate.convertAndSend(destination, new Greeting(message)); // Send the message
    }

    public void broadcast(LinkedList<String> usernames, String message) {
        for(int i = 0; i < usernames.size(); i++) {
            String destination = "/user/" + usernames.get(i) + "/topic/private-message"; // Specify the user's destination
            messagingTemplate.convertAndSend(destination, new Greeting(message)); // Send the message
        }

    }
}