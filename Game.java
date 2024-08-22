import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Game implements Runnable{
    private boolean rule;
    private LinkedList<Card> pile;
    private String[] playedCards;

    public Game() {
        this.rule = rule;
        this.pile = new LinkedList<Card>();
        this.playedCards = playedCards;
    }

    private static int firstStartingPower(Player[] players, Card element) {
        for(int i = 0; i < players.length; i++){
            LinkedList<Card> playerCards = players[i].getPlayerCards();
            for(int j = 0; j < playerCards.size(); j++){
                Card testElement = playerCards.get(j);
                if(testElement.getRank() == 1 && testElement.getSuit() == "Spades"){
                    System.out.printf("Player " + (i + 1) + " Starts the game.");
                    players[i].setPower(true);
                    return i;

                }
            }
        }
        return -1;
    }
public void run() {
    Deck deckofCards = new Deck();
    LinkedList<Card> deckList = deckofCards.getDeck();
    int playerCount = Server.getPlayerCount();
    Player[] players = new Player[playerCount];
    LinkedList[] playerCards = new LinkedList[playerCount];
    for (int i = 0; i < playerCount; i++) {
        playerCards[i] = new LinkedList<Player>();
    }
    Server.ClientHandler client =  Server.getPlayerClient();
    players = Player.addCardstoPlayers(0, players, deckList, playerCards, playerCount);
    for (int i = 0; i < players.length; i++) {
        players[i].getClientType().sendMessage("You are player " + (i + 1));
    }
    System.out.printf("Starting game with " + playerCount + " players...");
    round(players);
}
    public void round(Player[] players) {

        System.out.println("Starting game...");
        Card element = new Card("Spades", 1);
        firstStartingPower(players, element);
        while (true) {
                int currentPlayer = 0;
                for (int i = 0; i < players.length; i++) {
                    if (players[i].getPower()) {
                        currentPlayer = i;
                    }
                }
                int yourMove = currentPlayer + 1;
                System.out.println("Player " + yourMove + ", please state the card rule:");
                int rule = Integer.parseInt(players[currentPlayer].getClientType().getPlayerInput());
                System.out.println("The rule is " + rule + "!");
                roundStart(currentPlayer, players, rule);
        }
    }

    private int roundStart(int i, Player[] players, int rule) {
        if (i >= players.length) {
            i = 0; // Loop back to the first player if needed
        }
            if (!players[i].isPass()) {
                players[i].getClientType().sendMessage("Player " + (i + 1) + ", Your move");
                players[i].getClientType().sendMessage("Your available cards: " + players[i].getPlayerCards());
                String input = players[i].getClientType().getPlayerInput();
                System.out.println(input);
                if (input.equals("bs")) {
                    contest();
                } else if (input.equals("pass")) {
                    System.out.println("Player " + (i + 1) + " passed.");
                    players[i].setPass(true);
                    return roundStart(i + 1, players, rule); // Move to the next player
                } else {
                    move(input, i, players, rule);
                }
            }

        return roundStart(i + 1, players, rule); // Move to the next player
    }

    private void contest(){

    }

    private void move(String input, int i, Player[] players, int rule) {
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
            LinkedList<Card> checkForCard = players[i].getPlayerCards();
            int index = checkForCard.indexOf(checkPlayerHand);
                if (index != -1) {
                    pile.add(players[i].getPlayerCards().remove(index));
                }
                else{
                    System.out.println("Error!: You do not have this card. Please try again.");
                }
        }
        System.out.println("Player " + (i + 1) + " plays: " + playedCards.length + " " + rule + "!" );

    }

    @Override
    public String toString() {
        return "Game{" +
                "playedCards=" + Arrays.toString(playedCards) +
                '}';
    }
}





