package com.example.messagingstompwebsocket;

public class CardData {
    private String playerCards;
    private int playerCount;
    private String opponentCards;

    public CardData() {
    }

    public CardData(String playerCards, int playerCount) {
        this.playerCards = playerCards;
        this.playerCount = playerCount;
    }

    public String getPlayerCards() {
        return playerCards;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
