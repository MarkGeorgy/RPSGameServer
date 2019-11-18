//Name: Mark Georgy
//ID: 40024848



import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Arrays;


public class Client {

    private static Socket clientSocket;
    private static String playerName;
    private static JComboBox playersList;
    private static MyComboBoxModel myComboBoxModel;
    private static String[] temp;
    private static  JButton rockButton;
    private static  JButton paperButton;
    private static  JButton scissorsButton;
    private static  JButton playButton;
    private static JLabel resultLabel;
    private static JTextField nameTextField;


    public static void main(String[] args) throws Exception {

        //Create the GUI frame and components
        JFrame frame = new JFrame();
        frame.setBounds(100, 100, 450, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("RPS Game Client");
        frame.getContentPane().setLayout(null);

        JLabel clientNameLabel = new JLabel("");
        clientNameLabel.setBounds(10, 22, 200, 20);
        frame.getContentPane().add(clientNameLabel);
        clientNameLabel.setText("Client Name: ");

        nameTextField = new JTextField("");
        nameTextField.setBounds(100, 20, 170, 27);
        frame.getContentPane().add(nameTextField);

        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(280, 20, 100, 25);
        frame.getContentPane().add(connectButton);

        JLabel playWithLabel = new JLabel("");
        playWithLabel.setBounds(10, 62, 200, 20);
        frame.getContentPane().add(playWithLabel);
        playWithLabel.setText("Play With: ");

        playersList = new JComboBox<>();
        playersList.setBounds(100,60,170,27);
        frame.getContentPane().add(playersList);

        playButton = new JButton("Play");
        playButton.setBounds(280, 60, 100, 25);
        frame.getContentPane().add(playButton);

        rockButton = new JButton("ROCK");
        rockButton.setBounds(120, 200, 200, 50);
        frame.getContentPane().add(rockButton);
        rockButton.setVisible(false);

        paperButton = new JButton("PAPER");
        paperButton.setBounds(120, 270, 200, 50);
        frame.getContentPane().add(paperButton);
        paperButton.setVisible(false);

        scissorsButton = new JButton("SCISSORS");
        scissorsButton.setBounds(120, 340, 200, 50);
        frame.getContentPane().add(scissorsButton);
        scissorsButton.setVisible(false);

        resultLabel = new JLabel("");
        resultLabel.setBounds(10, 500, 300, 20);
        frame.getContentPane().add(resultLabel);
        resultLabel.setForeground(Color.blue);
        resultLabel.setFont(new Font("ARIAL", Font.BOLD,15));
        resultLabel.setText("");


        rockButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    resultLabel.setText("");
                    resultLabel.setForeground(Color.blue);
                    String name= playersList.getSelectedItem().toString();
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("-Move" + "//" + "ROCK" + "\n");
                    outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("-Compute" + "//" + name + "\n");
                }
                catch (Exception ex){
                    System.out.println(ex.toString());
                }

            }});

        paperButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    resultLabel.setText("");
                    resultLabel.setForeground(Color.blue);
                    String name= playersList.getSelectedItem().toString();
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("-Move" + "//" + "PAPER"+ "\n");
                    outToServer.writeBytes("-Compute" + "//" + name + "\n");

                }
                catch (Exception ex){

                }
            }});

        scissorsButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    resultLabel.setText("");
                    resultLabel.setForeground(Color.blue);
                    String name= playersList.getSelectedItem().toString();
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("-Move" + "//" + "SCISSORS" + "\n");
                    outToServer.writeBytes("-Compute" + "//" + name + "\n");
                }
                catch (Exception ex){

                }
            }});


        playButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    if (playButton.getText().equals("Play")) {
                        resultLabel.setText("");
                        if (playersList.getSelectedItem().toString().equals("--Select Player--")) {
                        } else {
                            String name = playersList.getSelectedItem().toString();
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            outToServer.writeBytes("-Play" + "//" + name + "\n");
                        }
                    }
                    else {
                        String name = playersList.getSelectedItem().toString();
                        playButton.setText("Play");
                        playersList.setEnabled(true);
                        rockButton.setVisible(false);
                        paperButton.setVisible(false);
                        scissorsButton.setVisible(false);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes("-Stop" + "//" + name + "\n");
                    }
                }
                catch (Exception ex){
                    System.out.println(ex.toString());
                }
            }});

        //Action listener when connect button is pressed
        connectButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    if(nameTextField.getText().isEmpty() || nameTextField.getText().contains(",")){}
                    else if (connectButton.getText().equals("Connect")) { //if pressed to connect
                        //create a new socket to connect with the server application
                        clientSocket = new Socket ("localhost", 6789);
                        //call function StartThread
                        StartThread();
                        String sendSentence = nameTextField.getText() + "\n";
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        outToServer.writeBytes("-Users" + "//" + sendSentence);

                        //make the GUI components visible, so the client can send and receive message
                        nameTextField.setEditable(false);
                        //change the Connect button text to disconnect
                        connectButton.setText("Disconnect");

                    } else { //if pressed to disconnect

                        //create an output stream and send a Remove message to disconnect from the server
                        DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());
                        outToServer.writeBytes("-Remove\n");
                        String name = playersList.getSelectedItem().toString();
                        outToServer.writeBytes("-Disconnect" + "//" + name + "\n");
                        //close the client's socket
                        clientSocket.close();
                        //make the GUI components invisible
                        nameTextField.setEditable(true);
                        playButton.setText("Play");
                        playersList.setEnabled(true);
                        rockButton.setVisible(false);
                        paperButton.setVisible(false);
                        scissorsButton.setVisible(false);
                        //change the Connect button text to connect
                        connectButton.setText("Connect");
                        resultLabel.setText("");

                    }

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }});


        //Disconnect on close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {

                    //create an output stream and send a Remove message to disconnect from the server
                    DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());
                    outToServer.writeBytes("-Remove\n");
                    //close the client's socket
                    clientSocket.close();
                    //change the Connect button text to connect
                    connectButton.setText("Connect");


                    System.exit(0);

                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        });

        frame.setVisible(true);
    }




    //Thread to always read messages from the server and print them in the textArea
    private static void StartThread() {

        new Thread (new Runnable(){ @Override
        public void run() {
            try {
                //create a buffer reader and connect it to the socket's input stream
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String receivedSentence;

                //always read received messages and append them to the textArea
                while (true) {

                    receivedSentence = inFromServer.readLine();
                    System.out.println(receivedSentence);
                    if (receivedSentence.startsWith("-Connect")) {
                    }
                    else if (receivedSentence.startsWith("-SendList")) {
                        String[] receivedMessage = receivedSentence.split(",");


                        receivedMessage[0] = "--Select Player--";
                        String name = nameTextField.getText();
                        for(int i = 0; i<receivedMessage.length; i++){
                            if (receivedMessage[i].equals(name))
                            {
                               receivedMessage=removeElement(receivedMessage, i);
                            }
                        }

                        if (temp == null) {
                            myComboBoxModel = new MyComboBoxModel(receivedMessage);
                            playersList.setModel(myComboBoxModel);
                            temp = receivedMessage;
                        } else if (Arrays.equals(temp, receivedMessage)) {
                        } else {
                            if(playerName == null){
                                myComboBoxModel = new MyComboBoxModel(receivedMessage);
                                playersList.setModel(myComboBoxModel);
                                temp = receivedMessage;
                            }
                            else
                            {
                                myComboBoxModel = new MyComboBoxModel(receivedMessage);
                                playersList.setModel(myComboBoxModel);
                                temp = receivedMessage;
                                playersList.setSelectedItem(playerName);
                            }

                        }

                    } else if (receivedSentence.startsWith("-Accept")) {
                        resultLabel.setText("");
                        String[] receivedMessage = receivedSentence.split(",");
                        if (receivedMessage[2].equals("START")) {
                            rockButton.setVisible(true);
                            paperButton.setVisible(true);
                            scissorsButton.setVisible(true);
                            rockButton.setEnabled(true);
                            paperButton.setEnabled(true);
                            scissorsButton.setEnabled(true);
                            playersList.setSelectedItem(receivedMessage[1]);
                            playerName = receivedMessage[1];
                            playersList.setEnabled(false);

                            playButton.setText("Stop");
                        } else {
                            resultLabel.setText(receivedMessage[1]);
                            resultLabel.setForeground(Color.RED);
                        }

                    } else if (receivedSentence.startsWith("-Result")) {
                        String[] receivedMessage = receivedSentence.split(",");
                        resultLabel.setText(receivedMessage[1]);
                        resultLabel.setForeground(Color.blue);
                        rockButton.setEnabled(true);
                        paperButton.setEnabled(true);
                        scissorsButton.setEnabled(true);
                    }
                    else if(receivedSentence.startsWith("-Wait")) {
                        String[] receivedMessage = receivedSentence.split(",");
                        rockButton.setEnabled(false);
                        paperButton.setEnabled(false);
                        scissorsButton.setEnabled(false);
                        resultLabel.setText(receivedMessage[1]);
                    }
                    else if(receivedSentence.startsWith("-Reset")) {
                        String[] receivedMessage = receivedSentence.split(",");
                            playButton.setText("Play");
                            playersList.setEnabled(true);
                            rockButton.setVisible(false);
                            paperButton.setVisible(false);
                            scissorsButton.setVisible(false);
                            resultLabel.setForeground(Color.RED);
                            resultLabel.setText(playerName + receivedMessage[1]);
                    }
                    else if(receivedSentence.startsWith("-Off")) {
                        String[] receivedMessage = receivedSentence.split(",");
                        playButton.setText("Play");
                        playersList.setEnabled(true);
                        rockButton.setVisible(false);
                        paperButton.setVisible(false);
                        scissorsButton.setVisible(false);
                        resultLabel.setForeground(Color.RED);
                        resultLabel.setText(playerName + receivedMessage[1]);
                    }
                }
            }
            catch(Exception ex) { }


        }}).start();

    }

    public static String[] removeElement(String[] original, int element){
        String[] n = new String[original.length - 1];
        System.arraycopy(original, 0, n, 0, element );
        System.arraycopy(original, element+1, n, element, original.length - element-1);
        return n;
    }

    static class MyComboBoxModel extends DefaultComboBoxModel {
        public MyComboBoxModel(String[] items) {
            super(items);
        }

    }
}