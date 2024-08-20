import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Deck deckofCards = new Deck();
        LinkedList<Card> deckList = deckofCards.getDeck();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter below how many players you would like to play with");
        int playerCount = scanner.nextInt();
        Player [] players = new Player[playerCount];
        LinkedList[] playerCards = new LinkedList[playerCount];
        for(int i = 0; i < playerCount; i++){
            playerCards[i] = new LinkedList<Player>();
        }
        players = Player.addCardstoPlayers(0, players, deckList , playerCards, playerCount);
        Game newRound = new Game();
        newRound.round(players);
        System.out.println(Arrays.toString(players));

    }


}
