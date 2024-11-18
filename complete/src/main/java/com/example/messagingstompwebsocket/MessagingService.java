package com.example.messagingstompwebsocket;

import game.logic.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;



@Service
public class MessagingService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendPrivateMessage(String username, String message, String gameId) {
        String destination = "/user/" + username + "/topic/private-message/" + gameId; // Specify the user's destination
        messagingTemplate.convertAndSend(destination, new Greeting(message)); // Send the message
    }

    public void opponentCardCountUpdater(String username, String[] cardCount, String gameId) {
        String destination = "/user/" + username + "/topic/opponent-card-count/" + gameId; // Specify the user's destination
        messagingTemplate.convertAndSend(destination, cardCount); // Send the message
    }
    public void cardUpdater(String username, LinkedList<Card> cards, String gameId) {
        String destination = "/user/" + username + "/topic/display-cards/" + gameId; // Specify the user's destination
        String[] convertedCards = new String[cards.size()];
        for(int i = 0; i < cards.size(); i++) {
            String rank = String.valueOf(cards.get(i).getRank());
            String[] cardSuit = cards.get(i).getSuit().split("");
            if(cardSuit[0].equals("S")){
                cardSuit[0] = "♠";
            }
            if(cardSuit[0].equals("H")){
                cardSuit[0] = "♥";
            }
            if(cardSuit[0].equals("D")){
                cardSuit[0] = "♦";
            }
            if(cardSuit[0].equals("C")){
                cardSuit[0] = "♣";
            }
            if(rank.equals("1")) {
                rank = "A";
            }
            if(rank.equals("11")) {
                rank = "J";
            }
            if(rank.equals("12")) {
                rank = "Q";
            }
            if(rank.equals("13")) {
                rank = "K";
            }
            if(rank.equals("14")) {
                rank = "Joker";
            }
            convertedCards[i] = cardSuit[0] + " " + rank;
        }
        messagingTemplate.convertAndSend(destination, convertedCards); // Send the message
    }

    public void broadcast(LinkedList<String> usernames, String message, String gameId) {
        for(int i = 0; i < usernames.size(); i++) {
            String destination = "/user/" + usernames.get(i) + "/topic/private-message/" + gameId; // Specify the user's destination
            messagingTemplate.convertAndSend(destination, new Greeting(message)); // Send the message
        }

    }
}