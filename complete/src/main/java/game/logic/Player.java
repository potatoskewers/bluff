package game.logic;

import com.example.messagingstompwebsocket.MessagingService;

import java.util.HashMap;
import java.util.LinkedList;

public class Player {
    private final String playerID;
//    private Server.ClientHandler clientType;
    private static HashMap<String, Player> playerMapping = new HashMap<String, Player>();
    ;
    private LinkedList<Card> playerCards;
    private int rule;
    private boolean power;
    private boolean pass;
    static MessagingService messagingService = new MessagingService();

    public void setPass(boolean pass) {
        this.pass = pass;
    }

//    public Server.ClientHandler getClientType() {
//        return clientType;
//    }
//
//    public void setClientType(Server.ClientHandler clientType) {
//        this.clientType = clientType;
//    }

    public String getPlayerID() {
        return playerID;
    }

    public boolean isPass() {
        return pass;
    }

    public LinkedList<Card> getPlayerCards() {
        return playerCards;
    }

    public boolean getPower() {
        return power;
    }

    public boolean setPower(boolean power) {
        this.power = power;
        return power;
    }

    public int getRule() {
        return rule;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }

    public Player(String playerID, LinkedList<Card> playerCards, boolean power, boolean pass) {
        this.playerID = playerID;
//        this.clientType = clientType;
        this.playerCards = playerCards;
        this.rule = rule;
        this.power = power;
        this.pass = pass;
    }

    public static HashMap<String, Player> getPlayerMapping() {
        return playerMapping;
    }

    public static Player[] addCardstoPlayers(LinkedList<String> playerIDs, int i, Player[] players, LinkedList<Card> deckofCards, LinkedList<Card>[] playerCards, int playerCount) {
//            LinkedList<Server.ClientHandler> clientList = Server.getPlayerClient();
            if (deckofCards.isEmpty()) {
                i = 0;
                for (int j = 0; j < playerCount; j++) {
//                    Server.ClientHandler client = clientList.get(j);
                    players[j] = new Player(playerIDs.get(i), playerCards[j], false, false);
                    playerMapping.put(playerIDs.get(i),players[j]);
                    i++;
                }
                return players;
            }
            if (i == playerCount) {
                i = 0;
            }
            playerCards[i].add(deckofCards.pop());
            return addCardstoPlayers(playerIDs, i + 1, players, deckofCards, playerCards, playerCount);

        }

    @Override
    public String toString() {
        return "Player{" +
                "playerCards=" + playerCards +
                ", rule=" + rule +
                ", power=" + power +
                ", pass=" + pass +
                '}';
    }
}
