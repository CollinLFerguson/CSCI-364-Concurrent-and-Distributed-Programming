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

        if(args.length != 2) {
            System.out.println("Usage: java HiLoClient <hostname> <port number>");
            System.exit(0);
        }

        hostName = args[0];
        portNumber = Integer.parseInt(args[1]);

        try {
            serverSocket = new Socket(hostName, portNumber);
        } catch (UnknownHostException e) {
            //e.printStackTrace(); // Change Later
            System.out.println("There were issues connecting to the server.");
            System.exit(0);
        } catch (IOException e) {
            //e.printStackTrace(); //Change Later
            System.out.println("There were issues connecting to the server.");
            System.exit(0);
        }

        PrintWriter out = null;
        BufferedReader in = null;

        try {
            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("There was an issue connecting the input/output of the server.");
            e.printStackTrace();
            System.exit(0);
        }

        System.out.println("Server: " + in.readLine());

        Scanner userInput = new Scanner(System.in);
        String nextGuess = " ";

        boolean isGameOver = false;
        while (!isGameOver) {
            System.out.print("Client: ");
            nextGuess = userInput.next();

            out.println(nextGuess);
            String serverMessage = in.readLine();

            System.out.println("Server: " + serverMessage);

            //Thread.sleep(100);

            if(serverMessage.contains("Correct")) {
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
