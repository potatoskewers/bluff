package game.logic;

import com.example.messagingstompwebsocket.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class Game implements Runnable {
    private int rule;
    private Player[] players;
    private LinkedList<String> playerIDs;
    private LinkedList<Card> pile;
    private String[] playedCards;
    private LinkedList<Card> clientPlayedCards;
    private int firstPlayer;
    private int currentPlayer;
    private Thread currentThread;
    private String[] opponentCardCount;

    private final MessagingService messagingService;
    private final String gameId;

    @Autowired
    public Game(LinkedList<String> playerIDs, MessagingService messagingService, String gameId) {
        this.playerIDs = playerIDs;
        this.messagingService = messagingService;
        this.gameId = gameId;
        this.pile = new LinkedList<>();
        this.clientPlayedCards = new LinkedList<>();
    }

    public void startingGame() {

        System.out.println("Starting the round!");

        Deck deckOfCards = new Deck();
        LinkedList<Card> deckList = deckOfCards.getDeck();
        int playerCount = playerIDs.size();
        players = new Player[playerCount];
        LinkedList<Card>[] playerCards = new LinkedList[playerCount];

        for (int i = 0; i < playerCount; i++) {
            playerCards[i] = new LinkedList<>();
        }

        players = Player.addCardstoPlayers(playerIDs, 0, players, deckList, playerCards, playerCount);

        for (int i = 0; i < players.length; i++) {
            sendMessage("You are player " + (i + 1), players[i].getPlayerID());
        }

        System.out.printf("Starting game with %d players...%n", playerCount);

        firstPlayer = findFirstPlayerWithStartingPower();
        currentPlayer = firstPlayer;
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    public int getRule() {
        return rule;
    }

    public Player[] getPlayers() {
        return players;
    }

    public LinkedList<Card> getClientPlayedCards() {
        return clientPlayedCards;
    }

    public LinkedList<Card> getPile() {
        return pile;
    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    private int findFirstPlayerWithStartingPower() {
        for (int i = 0; i < players.length; i++) {
            LinkedList<Card> playerCards = players[i].getPlayerCards();
            for (Card card : playerCards) {
                if (card.getRank() == 1 && card.getSuit().equals("Spades")) {
                    players[i].setPower(true);
                    broadcast("Player " + (i + 1) + " starts the game.");
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        startRound();
    }

    private void startRound() {
        for (int i = 0; i < players.length; i++) {
            updateCard(players[i].getPlayerCards(), players[i].getPlayerID());
            updateOpponentCard(players[i].getPlayerID(), i);
        }

        System.out.println("Starting the round!");

        try {
            String playerId = players[firstPlayer].getPlayerID();
            sendMessage("Player " + (firstPlayer + 1) + ", please state the card rule:", playerId);
            rule = Integer.parseInt(InputHandler.getPlayerInput(playerId));
            broadcast("The rule is " + rule + "!");
            playTurn(rule, 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in input handling");
        }
    }

    private int playTurn(int rule, int moveCounter) {
        if (currentPlayer >= players.length) {
            currentPlayer = 0;
        }
        String playerId = players[currentPlayer].getPlayerID();

        if (!players[currentPlayer].isPass()) {
            sendMessage("Player " + (currentPlayer + 1) + ", your move.", playerId);
            String input = InputHandler.getPlayerInput(playerId);

            if (input.equals("pass")) {
                System.out.println("Player " + (currentPlayer + 1) + " passed.");
                players[currentPlayer].setPass(true);
                currentPlayer++;
                return playTurn(rule, moveCounter);
            }
            else {
                processMove(input, currentPlayer, rule);
            }
        }

        currentPlayer++;
        return playTurn(rule, moveCounter + 1);
    }

    private void processMove(String input, int playerIndex, int rule) {
        clientPlayedCards.clear();
        playedCards = input.split(" ");

        for (String cardString : playedCards) {
            String suit = cardString.substring(0, 1);
            if (suit.equals("h")) {
                suit = "Hearts";
            } else if (suit.equals("d")) {
                suit = "Diamonds";
            } else if (suit.equals("s")) {
                suit = "Spades";
            } else if (suit.equals("c")) {
                suit = "Clubs";
            }

            int rank = Integer.parseInt(cardString.substring(1));
            Card card = new Card(suit, rank);

            int index = players[playerIndex].getPlayerCards().indexOf(card);
            if (index != -1) {
                pile.add(players[playerIndex].getPlayerCards().remove(index));
                clientPlayedCards.add(card);
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

    public void updateOpponentCard(String username, int index) {
        String[] opponentCardCount = new String[players.length];
        for (int i = 0; i < players.length; i++) {
            if (i != index) {
                opponentCardCount[i] = i + "." + getPlayers()[i].getPlayerCards().size();
            }
        }
        messagingService.opponentCardCountUpdater(username, opponentCardCount, gameId);
    }


    public void broadcast(String message) {
        messagingService.broadcast(playerIDs, message, gameId);
    }

    public void sendMessage(String message, String username) {
        messagingService.sendPrivateMessage(username, message, gameId);
    }

    // Getters and other helper methods as needed...
}
