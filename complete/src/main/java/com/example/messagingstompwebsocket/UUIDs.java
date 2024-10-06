package com.example.messagingstompwebsocket;

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
}