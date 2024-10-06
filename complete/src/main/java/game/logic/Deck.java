package game.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {

    @Override
    public String toString() {
        return "Deck{" +
                "deck=" + Arrays.toString(deck) +
                '}';
    }

    public LinkedList<Card> getDeck() {
        List<Card> decked = Arrays.asList(deck);
        LinkedList<Card> deck = new LinkedList<Card>(decked);
        return deck;
    }

    public Card[] deck;


    public Deck() {
        Card[] deck = new Card[54];
        for (int i = 0; i < 13; i++) {
            deck[i] = new Card("Spades", i + 1);
        }
        for (int i = 13; i < 26; i++) {
            deck[i] = new Card("Hearts", i - 12);
        }
        for (int i = 26; i < 39; i++) {
            deck[i] = new Card("Diamonds", i - 25);
        }
        for (int i = 39; i < 52; i++) {
            deck[i] = new Card("Clubs", i - 38);
        }
        for(int i = 52; i < 54; i++){
            deck[i] = new Card("Joker", 14);
        }
        List<Card> deckList = Arrays.asList(deck);

        Collections.shuffle(deckList);

        this.deck = deck;
    }

}
