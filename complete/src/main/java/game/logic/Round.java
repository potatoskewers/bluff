package game.logic;

import com.example.messagingstompwebsocket.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

import static game.logic.Game.playerIDs;
@Component
public class Round implements Runnable{
    private static int firstPlayer;
    private static String[] playedCards;
    private static int rule;
    private static LinkedList<Card> pile;
    private static LinkedList<Card> clientPlayedCards;
    private static int currentPlayer;
    private static Player[] players;
    private static Thread currentThread;
    private static MessagingService messagingService;

    @Autowired
    public Round(MessagingService messagingService) {
        this.rule = rule;
        this.pile = new LinkedList<Card>();
        this.clientPlayedCards = new LinkedList<Card>();
        this.playedCards = playedCards;
        players = Game.getPlayers();
        this.messagingService = messagingService;

    }

    public static int getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(int currentPlayer) {
        Round.currentPlayer = currentPlayer;
    }

    @Override
    public void run() {
        System.out.println("starting a round thread!");
        currentThread = Thread.currentThread();
        for (int i = 0; i < players.length; i++) {
            if (players[i].getPower()) {
                firstPlayer = i;
            }
        }
        int yourMove = firstPlayer + 1;
        sendMessage("Player " + yourMove + ", please state the card rule:", players[firstPlayer].getPlayerID());
        sendMessage("Player " + yourMove + "Your available cards: ", players[firstPlayer].getPlayerID());
        rule = Integer.parseInt(InputHandler.getPlayerInput(players[firstPlayer].getPlayerID()));
        broadcast("The rule is " + rule + "!");
        System.out.println(("The rule is " + rule + "!"));
        int moveCounter = 0;
        Game.getPlayers();
        currentPlayer = firstPlayer;
        roundStart(players, rule, moveCounter);
    }

    private int roundStart(Player[] players, int rule, int moveCounter) {
        if (currentPlayer >= players.length) {
            currentPlayer = 0; // Loop back to the first player if needed
        }
        String ID = players[currentPlayer].getPlayerID();
        if (!players[currentPlayer].isPass()) {
                sendMessage("Player " + (currentPlayer + 1) + ", Your move. ", ID);
                sendMessage("Player " + (currentPlayer + 1) + "Your available cards: " + players[currentPlayer].getPlayerCards() + "You have: " + players[currentPlayer].getPlayerCards().size() + " left.", ID);
                String input = InputHandler.getPlayerInput(ID);
                if (input.equals("pass")) {
                    System.out.println("Player " + (currentPlayer + 1) + " passed.");
                    players[currentPlayer].setPass(true);
                    currentPlayer = currentPlayer + 1;
                    return roundStart(players, rule, moveCounter); // Move to the next player
                }
                if (input.equals("bs")) {
                    System.out.println("handling BS clause");
                    return 1; //break out of loop
                } else {
                    move(input, currentPlayer, players, rule);
                }

        }
        currentPlayer = currentPlayer + 1;
        return roundStart(players, rule, moveCounter + 1); // Move to the next player
    }

    private void contest(){

    }

    private void move(String input, int i, Player[] players, int rule) {
        if(clientPlayedCards.size() != 0) {
            for(int j = 0; j < clientPlayedCards.size(); j++ ){
                clientPlayedCards.remove(j);
            }
        }
        playedCards = input.split(" ");
//                    System.out.println(playedCards.length);
        for (int j = 0; j < playedCards.length; j++) {
            String suit = playedCards[j].substring(0, 1);
            if(suit.equals("h")){
                suit = "Hearts";
            }
            if(suit.equals("d")){
                suit = "Diamonds";
            }
            if(suit.equals("s")){
                suit = "Spades";
            }
            if(suit.equals("c")){
                suit = "Clubs";
            }
            // Extract the number part and convert it to an integer
            int rank = Integer.parseInt(playedCards[j].substring(1));
            Card checkPlayerHand = new Card(suit, rank);
            int index = players[i].getPlayerCards().indexOf(checkPlayerHand);
            if (index != -1) {
                pile.add(players[i].getPlayerCards().remove(index));
                clientPlayedCards.add(checkPlayerHand);
            }
            else{
                System.out.println("Error!: You do not have this card. Please try again.");
            }
        }
        System.out.println("Player " + (i + 1) + " plays: " + playedCards.length + " " + rule + "!" );
        broadcast("Player " + (i + 1) + " plays: " + playedCards.length + " " + rule + "!" );

    }

    public static int getFirstPlayer() {
        return firstPlayer;
    }

    public static void setFirstPlayer(int firstPlayer) {
        Round.firstPlayer = firstPlayer;
    }

    public static String[] getPlayedCards() {
        return playedCards;
    }

    public static void setPlayedCards(String[] playedCards) {
        Round.playedCards = playedCards;
    }

    public static int getRule() {
        return rule;
    }

    public static void setRule(int rule) {
        Round.rule = rule;
    }

    public static Thread getCurrentThread() {
        return currentThread;
    }

    public static void setCurrentThread(Thread currentThread) {
        Round.currentThread = currentThread;
    }

    public static LinkedList<Card> getPile() {
        return pile;
    }

    public static void setPile(LinkedList<Card> pile) {
        Round.pile = pile;
    }

    public static LinkedList<Card> getClientPlayedCards() {
        return clientPlayedCards;
    }

    public static void setClientPlayedCards(LinkedList<Card> clientPlayedCards) {
        Round.clientPlayedCards = clientPlayedCards;
    }

    public static void broadcast(String message) {
        messagingService.broadcast(playerIDs, message);
    }

    public static void sendMessage(String message, String username) {
        messagingService.sendPrivateMessage(username, message);
    }

}
