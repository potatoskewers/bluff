import java.net.Socket;
import java.util.LinkedList;

public class Player {
    private Server.ClientHandler clientType;
    ;
    private LinkedList<Card> playerCards;
    private int rule;
    private boolean power;
    private boolean pass;

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public Server.ClientHandler getClientType() {
        return clientType;
    }

    public void setClientType(Server.ClientHandler clientType) {
        this.clientType = clientType;
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

    public Player( LinkedList playerCards , int rule, boolean power, boolean pass) {
        this.clientType = clientType;
        this.playerCards = playerCards;
        this.rule = rule;
        this.power = power;
        this.pass = pass;
    }


        public static Player[] addCardstoPlayers(int i, Player[] players, LinkedList<Card> deckofCards, LinkedList<Card>[] playerCards, int playerCount) {
            if (deckofCards.isEmpty()) {
                i = 0;
                for (int j = 0; j < playerCount; j++) {
                    players[j] = new Player(playerCards[i], 1, false, false);
                    players[j].setClientType(Server.playerStorage.removeFirst());
                    i++;
                }
                return players;
            }
            if (i == playerCount) {
                i = 0;
            }
            playerCards[i].add(deckofCards.pop());
            return addCardstoPlayers(i + 1, players, deckofCards, playerCards, playerCount);
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
