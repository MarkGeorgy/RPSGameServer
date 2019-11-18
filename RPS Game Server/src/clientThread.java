//Name: Mark Georgy
//ID: 40024848


import java.io.*;
import java.net.*;
import java.util.ArrayList;



public class clientThread extends Thread {

    //the ClientServiceThread class extends the Thread class and has the following parameters
    public String name;
    public Socket connectionSocket; //client connection socket
    public String movePlayed;
    public int number;
    public boolean inGame;
    ArrayList<clientThread> Clients; //list of all clients connected to the server

    //constructor function
    public clientThread(int number, Socket connectionSocket, ArrayList<clientThread> Clients, String userName, String movePlayed, boolean inGame) {
        this.number = number;
        this.movePlayed = movePlayed;
        this.connectionSocket = connectionSocket;
        this.Clients = Clients;
        this.name = userName;
        this.inGame=inGame;

    }

    //thread's run function
    public void run() {
        try {
            //create a buffer reader and connect it to the client's connection socket
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            ;
            String clientSentence;
            DataOutputStream outToClient;
            //always read messages from client
            while (true) {
                clientSentence = inFromClient.readLine();
                //check the start of the message
                if (clientSentence.startsWith("-Remove")) { //Remove Client
                    for (int i = 0; i < Clients.size(); i++) {
                        if (Clients.get(i).name == name) {
                            Clients.remove(i);
                        }
                    }
                }
                else if (clientSentence.startsWith("-Play")) {
                    String[] messageArray = clientSentence.split("//");
                    int sender = -1;
                    int receiver = -1;

                    for (int i = 0; i < Clients.size(); i++) {
                        if (Clients.get(i).name == name) {
                            sender = i;
                        }
                        if (Clients.get(i).name.equals(messageArray[1])) {
                            receiver = i;
                        }
                    }
                    if(!(Clients.get(receiver).inGame)) {
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Accept," + Clients.get(receiver).name + ",START" + "\n");
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Accept," + name + ",START" + "\n");
                        Clients.get(sender).inGame=true;
                        Clients.get(receiver).inGame=true;
                    }
                    else {
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Accept," + "Player selected is currently playing" + ",!START" + "\n");
                    }
                }

                else if (clientSentence.startsWith("-Stop")) {
                    String[] messageArray = clientSentence.split("//");
                    int receiver = -1;

                    for (int i = 0; i < Clients.size(); i++) {
                        if (Clients.get(i).name.equals(messageArray[1])) {
                            receiver = i;
                        }
                    }
                    inGame=false;
                    Clients.get(receiver).inGame=false;
                    outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                    outToClient.writeBytes("-Reset," + " has quit" + "\n");
                }

                else if (clientSentence.startsWith("-Disconnect")) {
                    String[] messageArray = clientSentence.split("//");
                    int receiver = -1;
                    for (int i = 0; i < Clients.size(); i++) {
                        if (Clients.get(i).name.equals(messageArray[1])) {
                            receiver = i;
                        }
                    }
                    if(inGame && Clients.get(receiver).inGame) {
                        inGame = false;
                        Clients.get(receiver).inGame = false;
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Off," + " has disconnected" + "\n");
                    }
                    else {

                    }
                }

                else if (clientSentence.startsWith("-Move")) {
                    String[] messageArray = clientSentence.split("//");
                    movePlayed=messageArray[1];
                    System.out.println(messageArray[1]);
                }

                else if (clientSentence.startsWith("-Compute")) {
                    String[] messageArray = clientSentence.split("//");
                    int sender = -1;
                    int receiver = -1;
                    System.out.println(messageArray[1]);

                    for (int i = 0; i < Clients.size(); i++) {
                        if (Clients.get(i).name == name) {
                            sender = i;
                        }
                        if (Clients.get(i).name.equals(messageArray[1])) {
                            receiver = i;
                        }
                    }
                    String senderMove = Clients.get(sender).movePlayed;
                    String receiverMove = Clients.get(receiver).movePlayed;
                    if (senderMove.equals("ROCK") && receiverMove == null) {
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Wait," + "Waiting for the other player" + "\n");
                    }
                    else if (senderMove.equals("PAPER") && receiverMove == null) {
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Wait," + "Waiting for the other player" + "\n");
                    }
                    else if (senderMove.equals("SCISSORS") && receiverMove == null) {
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Wait," + "Waiting for the other player" + "\n");
                    }

                   else if (senderMove.equals("ROCK") && receiverMove.equals("ROCK")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": Draw" +"\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": Draw" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }
                    else if (senderMove.equals("ROCK") && receiverMove.equals("PAPER")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": You win" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": " + Clients.get(receiver).name + " wins" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }
                    else if (senderMove.equals("ROCK") && receiverMove.equals("SCISSORS")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": " + Clients.get(sender).name + " wins" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": You win" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }
                    else if (senderMove.equals("PAPER") && receiverMove.equals("ROCK")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": " + Clients.get(sender).name + " wins" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": You win" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;

                    } else if (senderMove.equals("PAPER") && receiverMove.equals("PAPER")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": Draw" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": Draw" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }
                    else if (senderMove.equals("PAPER") && receiverMove.equals("SCISSORS")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": You win" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": " +Clients.get(receiver).name + " wins"  + "\n");

                    } else if (senderMove.equals("SCISSORS") && receiverMove.equals("ROCK")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": You win" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": " + Clients.get(receiver).name + " wins"  +  "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }
                    else if (senderMove.equals("SCISSORS") && receiverMove.equals("PAPER")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": " + Clients.get(sender).name + " wins" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": You win" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }
                    else if (senderMove.equals("SCISSORS") && receiverMove.equals("SCISSORS")) {
                        outToClient = new DataOutputStream(Clients.get(receiver).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + receiverMove + " X " + senderMove + ": Draw" + "\n");
                        outToClient = new DataOutputStream(Clients.get(sender).connectionSocket.getOutputStream());
                        outToClient.writeBytes("-Result," + senderMove + " X " + receiverMove + ": Draw" + "\n");
                        Clients.get(sender).movePlayed=null;
                        Clients.get(receiver).movePlayed=null;
                    }

                }
           }
        }
         catch (Exception ex) {
        }

    }

}


