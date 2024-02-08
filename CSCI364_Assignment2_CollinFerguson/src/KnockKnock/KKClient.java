/*
KnockKnock.KKClient.java
Collin L. Ferguson
Homework 2: Creating a multithreaded server/client that follow a specific protocol
*/

package KnockKnock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.Scanner;
import java.net.Socket;
import java.net.UnknownHostException;

public class KKClient{
    Socket connectionSocket;
    String hostName = " ";
    int portNumber = -1;
    PrintWriter out = null;
    BufferedReader in = null;
    Scanner userInput;

    /**
     * Validates the arguments supplied to the program. If not enough args are supplied, or a non-int is passed for the port, the program will exit.
     * @param args arguments for the program <IP> <Port>
     */
    private void validateArgs(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage java KnockKnock.KKClient <IP> <Port>");
            System.exit(0);
        }
        this.hostName = args[0];
        try {
            this.portNumber = Integer.parseInt(args[1]);
        }catch (Exception e){
            System.out.println("Usage java KnockKnock.KKClient <IP> <Port>");
            System.exit(0);
        }
    }

   /**
    * Starts the connection to the server. If a server matching the hostname and port cannot be found
    */
    private void connectToServer() {
        try{
            connectionSocket = new Socket(this.hostName, this.portNumber);
        } catch(UnknownHostException e) {
            System.out.println("There was an issue connecting to the server.\n" +
                    "ensure you entered the correct port and IP");
            System.exit(0);
        } catch(IOException e){
            System.out.println("There was an issue connecting to the server.\n" +
                    "Ensure you entered the correct port and IP");
            System.exit(0);
        }

        try {
            this.out = new PrintWriter(this.connectionSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("There was an issue connecting the input/output of the server.");
            System.exit(0);
        }

        System.out.println("Client: Connected to server successfully!");

    }

    /**
     * Method that plays the game itself. Takes turn replying to the server and waiting for the server's parts of the joke.
     */
    private void initiateJoke() {
        try {

            userInput = new Scanner(System.in);

            PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            String serverMessage = in.readLine();
            System.out.println("Rcvd: " + serverMessage);

            System.out.print("Enter reply to server: ");
            String userMessage = userInput.nextLine();
            out.println(userMessage);
            System.out.println("Sent:" + userMessage);

            serverMessage = in.readLine();
            System.out.println("Rcvd: " + serverMessage);

            System.out.print("Enter reply to server: ");
            userMessage = userInput.nextLine();
            out.println(userMessage);
            System.out.println("Sent:" + userMessage);

            serverMessage = in.readLine();
            System.out.println("Rcvd: " + serverMessage);

            serverMessage = in.readLine();
            System.out.println("Rcvd: " + serverMessage);

            System.out.print("Enter reply to server: ");
            userMessage = userInput.nextLine();
            out.println(userMessage);
            System.out.println("Sent:" + userMessage);


        } catch (SocketException e){
            System.out.println("Connection to the server was lost.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("There was an issue connecting the input/output of the server.");
            System.exit(0);
        }
    }

    /**
     * Main function of the program. It calls all supporting methods that connect it to a server and then plays out the protocol.
     * @param args
     */
    public static void main(String[] args) {
        KKClient client = new KKClient();
        client.validateArgs(args);
        client.connectToServer();
        client.initiateJoke();
        try {
            client.connectionSocket.close();
        } catch (IOException ignored) {}
    }
}
