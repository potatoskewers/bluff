package game.logic;

import com.example.messagingstompwebsocket.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class Game implements Runnable{
    private static int rule;
    private static Player[] players;
    public static LinkedList<String> playerIDs;
    private static LinkedList<Card> pile;
    private static String[] playedCards;
    private static LinkedList<Card> clientPlayedCards;
    private static int firstPlayer;
    private static int currentPlayer;
    private static Thread currentThread;

    private static MessagingService messagingService; // CORRECT


    @Autowired
    public Game(LinkedList<String> playerIDs, MessagingService messagingService) {
        this.playerIDs = playerIDs;
        this.rule = rule;
        this.messagingService = messagingService;
    }

    private static int firstStartingPower(Player[] players) {
        for(int i = 0; i < players.length; i++){
            LinkedList<Card> playerCards = players[i].getPlayerCards();
            for(int j = 0; j < playerCards.size(); j++){
                Card testElement = playerCards.get(j);
                if(testElement.getRank() == 1 && testElement.getSuit() == "Spades"){
                    System.out.printf("Player " + (i + 1) + " Starts the game.");
                    broadcast("Player " + (i + 1) + " Starts the game.");
                    players[i].setPower(true);
                    return i;

                }
            }
        }
        return -1;
    }

    public void run() {
        currentThread = Thread.currentThread();
        System.out.println("Starting the game!");
//        Server.setStartGame();
    Deck deckofCards = new Deck();
    LinkedList<Card> deckList = deckofCards.getDeck(); //deck of cards in a linkedlist
    int playerCount = playerIDs.size();
    players = new Player[playerCount];
    LinkedList[] playerCards = new LinkedList[playerCount];
    for (int i = 0; i < playerCount; i++) {
        playerCards[i] = new LinkedList<Player>();
    }
    players = Player.addCardstoPlayers(playerIDs,0, players, deckList, playerCards, playerCount);
    for (int i = 0; i < players.length; i++) {
        sendMessage("You are player " + (i + 1), players[i].getPlayerID());
//        players[i].getClientType().setPlayerNumber(i);
    }
    System.out.printf("Starting game with " + playerCount + " players...");
        System.out.println("Starting game...");
        firstStartingPower(players);
        Round newRound = new Round(messagingService);
        new Thread(newRound).start();
}
    public static Player[] getPlayers() {
        return players;
    }

    public static void broadcast(String message) {
        messagingService.broadcast(playerIDs, message);
    }

    public static void sendMessage(String message, String username) {
        messagingService.sendPrivateMessage(username, message);
    }


    public static int getFirstPlayer() {
        return firstPlayer;
    }

    public static int getCurrentPlayer() {
        return currentPlayer;
    }

    public static LinkedList<Card> getPile() {
        return pile;
    }

    public static int getRule() {
        return rule;
    }

    public static Thread getCurrentThread() {
        return currentThread;
    }
}





