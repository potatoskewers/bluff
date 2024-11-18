package com.example.messagingstompwebsocket;

import game.logic.Game;

import java.util.HashMap;
import java.util.LinkedList;

public class UUIDs {
    private static LinkedList<String> UUIDs = new LinkedList<>();
    private final String PlayerList;
    private static HashMap<String, String> userIDs = new HashMap<>();

    public static LinkedList<String> getUUIDs() {
        return UUIDs;
    }

    public static HashMap<String, String> getUserIDs() {
        return userIDs;
    }

    public String getPlayerList() {
        return PlayerList;
    }

    public static void removeUser(int i) {
        UUIDs.remove(i);
    }

    public UUIDs(String PlayerList) {
        this.PlayerList = PlayerList;
    }

//    public static HashMap<Thread, Round> threadRoundMapping = new HashMap<>();

    public static HashMap<String, Game> gameIdentifier = new HashMap<>(); //contains gameID and game
    public static HashMap<String, String> playerToGame = new HashMap<>(); //contains sessionID and gameID


    //handling nonstatic playerlist

    public static HashMap<String, LinkedList<String>> playerListToGameID= new HashMap<>(); //contains playerList and gameID
    public static HashMap<String, LinkedList<String>> getPlayerListToGameID() {
        return playerListToGameID;
    }

    public static void playerToGameAdder(String userId, String gameId) {
        playerToGame.put(userId, gameId);
    }

    public static String retrieveGameID(String sessionId) {
        return playerToGame.get(sessionId);
    }

    public static HashMap<String, Game> getGameIdentifier() {
        return gameIdentifier;
    }

    public static HashMap<String, String> getPlayerToGame() {
        return playerToGame;
    }

    public static Game retrieveGame(String gameId) {
        System.out.println("retrieving game: " + gameIdentifier.get(gameId));
        return gameIdentifier.get(gameId);
    }



    public static void gameIdentifierAdder(Game game, String gameId) {
        gameIdentifier.put(gameId, game);
    }

}