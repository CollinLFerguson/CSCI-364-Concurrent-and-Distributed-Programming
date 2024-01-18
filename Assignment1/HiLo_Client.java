/*
 * HiLo_Client.java
 * Client part of a hi-lo guessing game that makes use of sockets.
 * Collin L. Ferguson
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.Scanner;
public class HiLo_Client{

    public static void main(String[] args) throws IOException, InterruptedException {
        String hostName = " ";
        int portNumber = -1;
        Socket serverSocket = null;
        System.out.println("Ant Test");

        if(args.length != 2) {
            System.out.println("Usage: java HiLoClient <hostname> <port number>");
            System.exit(0);
        }

        hostName = args[0];
        portNumber = Integer.parseInt(args[1]);

        try {
            serverSocket = new Socket(hostName, portNumber);
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Change Later
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace(); //Change Later
            System.exit(0);
        }

        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        System.out.println(in.readLine());

        Scanner userInput = new Scanner(System.in);
        String nextGuess = " ";

        boolean isGameOver = false;
        while (!isGameOver) {
            System.out.print("Client: ");
            nextGuess = userInput.next();

            out.println(nextGuess);

            System.out.println("Server: " + in.readLine());

            Thread.sleep(100);

            if(in.ready()) {
                System.out.println("Server: " + in.readLine());
                nextGuess = userInput.next();
                out.println(nextGuess);

                if (!nextGuess.equals("Y") && !nextGuess.equals("y")) {
                    isGameOver = true;
                } else {
                    System.out.println("Server: " + in.readLine());
                }
            }
        }

        serverSocket.close();
        out.close();
        in.close();
    }
}
