package game.logic;

import java.security.Principal;

public class InputHandler {
    public static String playerInput = null;
    private static final Object lock = new Object();
    private static Principal principal;

    public static String getWSInput() {
        return playerInput;
    }


    public static void setPlayerInput(String playerInput, Principal principal) {
        synchronized (lock) {
            InputHandler.principal = principal;
            InputHandler.playerInput = playerInput;
            lock.notifyAll();
        }

    }

    public static String getPlayerInput(String ID) {
        String input = null;
        synchronized (lock) {
            // Wait until playerInput is not null (i.e., new input is received)
            while (playerInput == null) {
                try {
                    lock.wait(); // Wait until a new input is received
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    return null;
                }
            }
            if(principal.toString().equals(ID)) {
                input = playerInput;
            }
            playerInput = null; // Clear the input after reading, ensuring it's ready for the next input
            return input;
        }
    }
}



