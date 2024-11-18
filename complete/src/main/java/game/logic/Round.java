package game.logic;

import com.example.messagingstompwebsocket.MessagingService;

import java.util.LinkedList;

public class Round implements Runnable {
    private int firstPlayer;
    private String[] playedCards;
    private int rule;
    private LinkedList<Card> pile;
    private LinkedList<Card> clientPlayedCards;
    private int currentPlayer;
    private Player[] players;
    private Thread currentThread;

    private final MessagingService messagingService;
    private final String gameId;
    private final LinkedList<String> playerIDs;

    public Round(Player[] players, MessagingService messagingService, String gameId, LinkedList<String> playerIDs) {
        this.players = players;
        this.messagingService = messagingService;
        this.gameId = gameId;
        this.playerIDs = playerIDs;
        this.pile = new LinkedList<>();
        this.clientPlayedCards = new LinkedList<>();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void run() {
        for (Player player : players) {
            updateCard(player.getPlayerCards(), player.getPlayerID());
        }

        System.out.println("Starting a round thread!");
        currentThread = Thread.currentThread();

        for (int i = 0; i < players.length; i++) {
            if (players[i].getPower()) {
                firstPlayer = i;
                break;
            }
        }

        int yourMove = firstPlayer + 1;
        sendMessage("Player " + yourMove + ", please state the card rule:", players[firstPlayer].getPlayerID());

        try {
            rule = Integer.parseInt(InputHandler.getPlayerInput(players[firstPlayer].getPlayerID()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in input handling");
        }

        broadcast("The rule is " + rule + "!");
        currentPlayer = firstPlayer;
        roundStart(rule, 0);
    }

    private int roundStart(int rule, int moveCounter) {
        currentThread = Thread.currentThread();
        if (currentPlayer >= players.length) {
            currentPlayer = 0;
        }
        String ID = players[currentPlayer].getPlayerID();

        if (!players[currentPlayer].isPass()) {
            sendMessage("Player " + (currentPlayer + 1) + ", Your move.", ID);
            String input = InputHandler.getPlayerInput(ID);

            if (input.equals("pass")) {
                System.out.println("Player " + (currentPlayer + 1) + " passed.");
                players[currentPlayer].setPass(true);
                currentPlayer++;
                return roundStart(rule, moveCounter);
            }

            if (input.equals("bs")) {
                System.out.println("Handling BS clause");
                return 1;
            } else {
                move(input, currentPlayer, rule);
            }
        }

        currentPlayer++;
        return roundStart(rule, moveCounter + 1);
    }

    private void move(String input, int playerIndex, int rule) {
        clientPlayedCards.clear();
        playedCards = input.split(" ");

        for (String cardString : playedCards) {
            String suit = cardString.substring(0, 1);
            suit = switch (suit) {
                case "h" -> "Hearts";
                case "d" -> "Diamonds";
                case "s" -> "Spades";
                case "c" -> "Clubs";
                default -> suit;
            };

            int rank = Integer.parseInt(cardString.substring(1));
            Card checkPlayerHand = new Card(suit, rank);
            int index = players[playerIndex].getPlayerCards().indexOf(checkPlayerHand);

            if (index != -1) {
                pile.add(players[playerIndex].getPlayerCards().remove(index));
                clientPlayedCards.add(checkPlayerHand);
            } else {
                System.out.println("Error: You do not have this card. Please try again.");
            }
        }
        broadcast("Player " + (playerIndex + 1) + " plays: " + playedCards.length + " " + rule + "!");
    }

    public void updateCard(LinkedList<Card> cards, String username) {
        System.out.println("Updating cards for user " + username + " with " + cards.size() + " cards");
        messagingService.cardUpdater(username, cards, gameId);
    }

    public void broadcast(String message) {
        messagingService.broadcast(playerIDs, message, gameId);
    }

    public void sendMessage(String message, String username) {
        messagingService.sendPrivateMessage(username, message, gameId);
    }
}