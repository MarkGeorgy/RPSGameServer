//Name: Mark Georgy
//ID: 40024848


import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.ArrayList;


import javax.swing.JFrame;
import javax.swing.JLabel;


public class Server {

    //Array of type ClientServiceThread, for all connected clients
    public static ArrayList<clientThread> Clients = new ArrayList<clientThread>();
    static int clientCount = 0;
    static String clientName="";
    static boolean inGame = false;
    static String movePlayed=null;

    public static void main(String[] args) throws Exception {

        //Create the GUI frame and components
        JFrame frame = new JFrame ("RPS Game Server");
        frame.setLayout(null);
        frame.setBounds(100, 100, 300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JLabel connectionStatusLabel = new JLabel("No Clients Connected");
        connectionStatusLabel.setBounds(80, 30, 200, 30);
        connectionStatusLabel.setForeground(Color.red);
        frame.getContentPane().add(connectionStatusLabel);

        //create the welcoming server's socket
        ServerSocket welcomeSocket = new ServerSocket(6789);
        //thread to always listen for new connections from clients
        new Thread (new Runnable(){ @Override
        public void run() {
            Socket connectionSocket;
            DataOutputStream outToClient;

            while (!welcomeSocket.isClosed()) {
                try {
                    //when a new client connect, accept this connection and assign it to a new connection socket
                    connectionSocket = welcomeSocket.accept();
                    String clientSentence;
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    //create a new output stream and send the message "You are connected" to the client
                    outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    outToClient.writeBytes("-Connected\n");
                    clientSentence = inFromClient.readLine();
                    clientCount++;
                    if (clientSentence.startsWith("-Users")) {
                        String[] messageArray = clientSentence.split("//");
                        clientName=messageArray[1];
                    }

                    //add the new client to the client's array
                    Clients.add(new clientThread(clientCount, connectionSocket, Clients, clientName, movePlayed, inGame));
                    //start the new client's thread
                    Clients.get(Clients.size() - 1).start();
                }
                catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        }}).start();



        //thread to always get the count of connected clients and update the label and send to clients
        new Thread (new Runnable() { @Override
        public void run() {
            try {
                DataOutputStream outToClient;
                while (true) {
                    if (Clients.size() > 0) //if there are one or more clients print their number
                    {

                        if (Clients.size() == 1)
                            connectionStatusLabel.setText("1 Client Connected");
                        else
                            connectionStatusLabel.setText(Clients.size() + " Clients Connected");
                        connectionStatusLabel.setForeground(Color.blue);
                    }
                    else { //if there are no clients connected, print "No Clients Connected"
                        connectionStatusLabel.setText("No Clients Connected");
                        connectionStatusLabel.setForeground(Color.red);
                    }

                    String playersNames="-SendList,";
                    for (int i = 0; i < Clients.size(); i++) {
                            playersNames = playersNames + Clients.get(i).name + ",";
                    }
                    for (int i = 0; i < Clients.size(); i++) {
                            outToClient = new DataOutputStream(Clients.get(i).connectionSocket.getOutputStream());
                            outToClient.writeBytes(playersNames.substring(0, playersNames.length() - 1) + "\n");
                            System.out.println(playersNames);
                        }

                    Thread.sleep(1000);
                }
            }

            catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }}).start();

        frame.setVisible(true);
    }
}