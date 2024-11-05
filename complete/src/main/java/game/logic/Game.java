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
    private static String gameId = null;


    @Autowired
    public Game(LinkedList<String> playerIDs, MessagingService messagingService, String gameId) {
        this.playerIDs = playerIDs;
        Game.gameId = gameId;
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
                    broadcast("Player " + (i + 1) + " Starts the game.", gameId);
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
        sendMessage("You are player " + (i + 1), players[i].getPlayerID(), gameId);
//        updateCard(players[i].getPlayerCards(), players[i].getPlayerID());
//        players[i].getClientType().setPlayerNumber(i);
    }
    System.out.printf("Starting game with " + playerCount + " players...");
        System.out.println("Starting game...");
        firstStartingPower(players);
        Round newRound = new Round(messagingService, gameId);
        new Thread(newRound).start();
}
    public static Player[] getPlayers() {
        return players;
    }

    public static void broadcast(String message, String gameId) {
        messagingService.broadcast(playerIDs, message, gameId);
    }

    public static void sendMessage(String message, String username, String gameId) {
        System.out.println("Sending" + message + " to " + username);
        messagingService.sendPrivateMessage(username, message, gameId);
    }


    public static String getGameId() {
        return gameId;
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





