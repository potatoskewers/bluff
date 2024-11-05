package game.logic;

import com.example.messagingstompwebsocket.MessagingService;
import com.example.messagingstompwebsocket.UUIDs;

import java.util.Arrays;
import java.util.LinkedList;

public class Contest {
    private static boolean lying = false;
    private final String sessionID;
    private static MessagingService messagingService;
    private static LinkedList<String> playerIDs;
    private static String gameId = "";
    private int lastPlayer;

    public Contest(String sessionID, MessagingService messagingService, LinkedList<String> playerIDs, String gameId) {
        this.playerIDs = playerIDs;
        this.sessionID = sessionID;
        this.messagingService = messagingService;
        this.gameId = gameId;
    }

    public void run() {
        String currentUUID = UUIDs.getUserIDs().get(sessionID);
        Player challengerPlayer = Player.getPlayerMapping().get(currentUUID);
        LinkedList<Card> pile = Round.getPile();
        LinkedList<Card> playedCards = Round.getClientPlayedCards();
        Player[] players = Game.getPlayers();
        int challengerNumber = Arrays.asList(Game.getPlayers()).indexOf(challengerPlayer);
        int lastPlayer = Round.getCurrentPlayer();
        if(lastPlayer == 0) {
            lastPlayer = players.length - 1;
        }
        else {
            lastPlayer -= 1;
        }
        Player defendingPlayer = players[lastPlayer];
        broadcast("Player " + (challengerNumber + 1) + " is contesting " + "Player " + (lastPlayer + 1) +"'s played cards!");
        broadcast("Player " + (challengerNumber + 1) + " is contesting " + (lastPlayer + 1) +"'s played cards!");

        Player previousFirstPlayer = players[Round.getFirstPlayer()];
        int rule = Round.getRule();
        checkWinner(playedCards, rule);
        System.out.println("Played cards: " + playedCards + "Rule: " + rule);
        if(!lying) {
            System.out.println("Player " + (lastPlayer + 1) + " was not lying!");
            broadcast("Player " + (lastPlayer + 1) + " was not lying!");
            sendMessage("You were wrong! You will pick up the pile now.", challengerPlayer.getPlayerID());
            for(int j = 0; j < Round.getPile().size(); j++){
                challengerPlayer.getPlayerCards().push(pile.pop());
            }
            defendingPlayer.setPower(true);
        }
        else {
            System.out.println("Player " + (lastPlayer + 1) + " was lying!");
            broadcast("Player " + (lastPlayer + 1) + " was lying!");
            sendMessage("You have been caught lying! You will pick up the pile now.", defendingPlayer.getPlayerID());
            for(int j = 0; j < Round.getPile().size(); j++){
                defendingPlayer.getPlayerCards().push(pile.pop());
            }
            challengerPlayer.setPower(true);


        }
        previousFirstPlayer.setPower(false);
        restorePass(players);
        for(int i = 0; i < players.length; i++){
            if(players[i].getPlayerCards().isEmpty()){
                System.out.println("GAME OVER. Player "+ (i + 1) +" wins the game!");


            }
        }


    }

    private static void checkWinner(LinkedList<Card> playedCards, int rule) {
        for(int i = 0; i < playedCards.size(); i++) {
            if (playedCards.get(i).getRank() != rule) {
                System.out.println("setting lying as true...");
                lying = true;
                break;
            }
        }
    }
    private static void restorePass(Player[] players) {
        for(int i = 0; i < players.length; i++){
            players[i].setPass(false);
        }
    }


    public static boolean isTruth() {
        return lying;
    }

    public static void broadcast(String message) {
        messagingService.broadcast(playerIDs, message, gameId);
    }

    public static void sendMessage(String message, String username) {
        messagingService.sendPrivateMessage(username, message, gameId);
    }

}
