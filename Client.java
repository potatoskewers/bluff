import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client class
class Client {

    private int playerCount;

    // driver code
    public static void main(String[] args)
    {
        int playerCount = 0;
        // establish a connection by providing host and port number
        try (Socket socket = new Socket("localhost", 1234)) {

            // writing to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // reading from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // create a separate thread to listen to server messages
            Thread serverListener = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            serverListener.start();  // start the thread to listen for server messages

            // object of scanner class for reading user input
            Scanner sc = new Scanner(System.in);
            String line = null;

            while (!"exit".equalsIgnoreCase(line)) {
                // reading from user
                line = sc.nextLine();

                // sending the user input to server
                out.println(line);
                out.flush();
                }

            // closing the scanner object
            sc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
