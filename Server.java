import java.io.*;
import java.net.*;
import java.util.LinkedList;

import static java.lang.System.out;

// Server class
class Server {
    static boolean isFirstClient = false;
    public static int playerCount = 0;
    static LinkedList<ClientHandler> playerStorage = new LinkedList<>();
    static boolean startGame = false;

    public static void setStartGame() {
        startGame = true;
    }

    public static void main(String[] args) {
        PrintWriter out = null;
        BufferedReader in = null;
        ServerSocket server = null;

        try {
            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);

            // running infinite loop for getting
            // client requests
            while (true) {
                // socket object to receive incoming client requests
                Socket client = server.accept();
                playerCount += 1;

                // Displaying that new client is connected to server
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());

                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client);

                // This thread will handle the client separately
                new Thread(clientSock).start();
                playerStorage.add(clientSock);
                System.out.println("Total players that have joined: " + playerCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void startGame() {
        startGame = true;
        Deck deckofCards = new Deck();
        LinkedList<Card> deckList = deckofCards.getDeck();
        Player[] players = new Player[playerCount];
        LinkedList[] playerCards = new LinkedList[playerCount];
        for (int i = 0; i < playerCount; i++) {
            playerCards[i] = new LinkedList<Player>();
        }
        players = Player.addCardstoPlayers(0, players, deckList, playerCards, playerCount);
        for (int i = 0; i < players.length; i++) {
            players[i].getClientType().sendMessage("You are player " + (i + 1));
        }
        System.out.printf("Starting game with " + playerCount + " players...");
        Game newRound = new Game();
        newRound.round(players);
    }

    // ClientHandler class
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerInput;
        private final Object lock = new Object();

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                // get the outputstream of client
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // get the inputstream of client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                messageHandler();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void messageHandler() throws IOException {
            String line;
            while ((line = in.readLine()) != null) {
                if ("Start".equalsIgnoreCase(line)) {
                    synchronized (Server.class) {
                        if (!startGame) {
                            line = null;
                            playerInput = null;
                            startGame();
                        } else {
                            out.println("Game has already started!");
                        }
                    }
                } else {
                    synchronized (lock){
                        playerInput = line;
                        lock.notifyAll();
                    }
                    out.println("Message received: " + line);
                    // Handle other client messages here if needed
                }
            }
        }

        public String getPlayerInput() {
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
                String input = playerInput;
                playerInput = null; // Clear the input after reading, ensuring it's ready for the next input
                return input;
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}