/*
KnockKnock.KKServer.java
Collin L. Ferguson
Homework 2: Creating a multithreaded server/client that follow a specific protocol
*/

package KnockKnock;

import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import java.util.Random;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Server class that will start, and then continuously wait for clients to connect with until System.in receives
 * the string "exit". Once a connection is made, a thread will be spawned off which will start the knock knock protocol.
 * @author Collin
 */
public class KKServer {
    private static final int MIN_PORT = 1024;
    private Random randomNumber;
    private int portNumber;
    private ServerSocket serverSocket;
    private String fileName;
    private ArrayList<String> jokeList;


    /**
     * Ensures that the args supplied to the program are valid. If any are not valid, the program will exit.
     * @param args The arguments supplied by the command line <TCP Port> <Joke File Name> <Random Number>
     */
    private void validateArgs(String[] args)
    {
        if (args.length < 3) {
            System.out.println("KnockKnock.KKServer <TCP Port> <Joke File Name> <Random Number>");
            System.exit(0);
        } else {
            try {
                long randomArg = Long.parseLong(args[2]);
                this.randomNumber = new Random(randomArg);

            } catch (Exception e) {
                System.out.println("KnockKnock.KKServer <TCP Port> <Joke File Name> <Random Number>");
                System.out.println("The random number must be a whole number.");
                System.exit(0);
            }
            try {
                this.portNumber = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("KnockKnock.KKServer <TCP Port> <Joke File Name> <Random Number>");
                System.out.println("The port number must be a whole number > 1024.");
                System.exit(0);
            }
            if(portNumber <= MIN_PORT) {
                System.out.println("The port number must be a whole number > 1024.");
                System.exit(0);
            }

            if(new File(args[1]).isFile()) {
                fileName = args[1];
            } else {
                System.out.println("That file does not exist");
                System.exit(0);
            }
        }
    }

    /**
     * Reads supplied file until an EOF marker is found. Populates the ArrayList joke.
     */
    private void readFile()
    {
        BufferedReader inFileReader = null;
        try {
            this.jokeList = new ArrayList<>(1);

            File inFile = new File(this.fileName);
            inFileReader = new BufferedReader(new FileReader(inFile));
            String currentJoke = inFileReader.readLine();

            while(currentJoke != null) {
                this.jokeList.add(currentJoke);
                currentJoke = inFileReader.readLine();
            }
            inFileReader.close();

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("The file cannot be found. Please check that no changes have been made to the file directory.");

        } catch(IOException e) {
            //e.printStackTrace();
            System.out.println("The file cannot be read. Please check that permissions allow this file to be read by programs.");
        }
    }

    /**
     * Creates the connection between server and client. After the connection is secured, a new thread will be spawned to facilitate the Knock-Knock protocol.
     * @author Collin L. Ferguson
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private void createConnection()
    {
        try {
            serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("Server socket failed to initialize.");
        }

        System.out.println("Server initialized correctly.\nWaiting for clients:");

        while (true) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                Runnable connection = new serverThread((this.jokeList.get(this.randomNumber.nextInt(this.jokeList.size()))),
                        clientSocket);
                // Takes a random element from jokeList by generating a random int value, then modulo
                Thread thread = new Thread(connection);
                thread.start();
            } catch (Exception e){
                //e.printStackTrace(); //TEMP//
                System.out.println("A connection could not be established with the client.");
            }
        }
    }

    /**
     * The main function of the program, creates the initial KKServer object, and runs all supporting methods.
     * @param args Arguments supplied to the program <TCP Port> <Joke File Name> <Random Number>
     */
    public static void main(String[] args) {
        KKServer server = new KKServer();
        server.validateArgs(args);
        server.readFile();
        Thread exitListener = new Thread(new ExitListener());
        exitListener.start();

        server.createConnection();
    }
}

/**
 *  This class creates a runnable that will watch System.in for the string "exit."
 *  Afterwards it will terminate the program.
 * Not required, but a graceful way to exit the program.
 * @author Collin L. Ferguson
 */
class ExitListener implements Runnable {
    public void run(){
        Scanner scanner = new Scanner(System.in);

        while(true) {
            if (scanner.next().equals("exit")){
                System.out.println("Server: Exit code supplied. Goodbye");
                System.exit(0);
            }
        }
    }
}
/**
 * Thread that manages the connection with the client and runs the game.
 * @author Collin Ferguson
 * @link serverThread
 */
class serverThread implements Runnable {
    /** @param clientSocket the connection to the client. If terminated, the object will likely become unable to function and close the thread.*/
    private final Socket clientSocket;
    /** @param joke String storing the joke to be relayed to the client*/
    private final String joke;
    /** @param in a buffered reader that works as the input for the client to send messages to the server.*/
    private BufferedReader in;
    /** @param out a print writer that works as the output for the server to send messages to the client.*/
    private PrintWriter out;

    serverThread(String joke, Socket clientSocket)
    {
        this.joke = joke;
        this.clientSocket = clientSocket;
    }

    /**
     * Method that plays the game itself. Takes turn asking the client a Knock-knock joke (initiation, setup, punchline, exit) and waiting for the client's reply.
     * If an unexpected message is passed to the server, it will be seen a breach of protocol and exit.
     * If connection to the client is interrupted, the server will exit.
     */
    private void initiateJoke(){
        try {
            out.println("Knock Knock");
            System.out.println("Sent: Knock Knock");

            String clientMessage;
            clientMessage = in.readLine();
            System.out.println("Rcvd: " + clientMessage);

            if(clientMessage == null || !clientMessage.equals("Who's there?"))
            {
                System.out.println("That response did not fit the protocol.\nGoodbye");
                out.println("That response did not fit the protocol.\nGoodbye");
                return;
            }

            String setup = (joke.split("#")[0].strip());

            out.println(setup);
            System.out.println("Sent: " + setup);

            clientMessage = in.readLine();
            System.out.println("Rcvd: " + clientMessage);

            if(clientMessage == null || !clientMessage.equals(setup + " who?"))
            {
                System.out.println("That response did not fit the protocol.\nGoodbye");
                out.println("That response did not fit the protocol.\nGoodbye");
                return;
            }

            String punchline = joke.split("#")[1].strip();
            out.println(punchline);
            System.out.println("Sent: " + punchline);

            out.println("Bye.");
            System.out.println("Sent: Bye");

            clientMessage = in.readLine();
            System.out.println("Rcvd: " + clientMessage);


        } catch (IOException e) {
            System.out.println("There was an issue talking to the the client.\nClosing this connection.");
            return;
        }
    }

    /**
     * The main function of the thread, calls all supporting methods.
     */
    public void run(){
        System.out.println("Client connected successfully!");

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an issue connecting IO with the client.");
        }

        if(joke != null){
            this.initiateJoke();
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("The client has already closed.");
            e.printStackTrace();
        }
    }
}
