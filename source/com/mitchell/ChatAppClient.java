package com.mitchell;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatAppClient {

        JTextArea incoming;
        JTextField outgoing;
        BufferedReader reader;
        PrintWriter writer;
        Socket sock;

        public static void main(String[] args) {
                ChatAppClient client = new ChatAppClient();
                client.go();
        }

        public void go() {
                JFrame frame = new JFrame("Mitchell's Chat App");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel mainPanel = new JPanel();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                incoming = new JTextArea(30,50);
                incoming.setLineWrap(true);
                incoming.setWrapStyleWord(true);
                incoming.setEditable(false);
                JScrollPane qScroller = new JScrollPane(incoming);
                qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                outgoing = new JTextField(40);
                JButton sendButton = new JButton("Send");
                sendButton.addActionListener(new SendButtonListener());
                mainPanel.add(qScroller);
                mainPanel.add(outgoing);
                mainPanel.add(sendButton);
                setUpNetworking();

                Thread readerThread = new Thread(new IncomingReader());
                readerThread.start();

                frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
                frame.setSize(800,500);
                frame.setVisible(true);
        } // close go

        private void setUpNetworking() {

                try {
                        sock = new Socket("192.168.1.165", 43210);
                        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
                        reader = new BufferedReader(streamReader);
                        writer = new PrintWriter(sock.getOutputStream());
                        System.out.println("networking established");
                } catch (IOException ex){
                        ex.printStackTrace();
                }
        } // close setUpNetworking

        public class SendButtonListener implements ActionListener {
                public void actionPerformed(ActionEvent ev) {
                        try {
                                writer.println(outgoing.getText());
                                writer.flush();

                        } catch(Exception ex) {
                                ex.printStackTrace();
                        }
                        outgoing.setText("");
                        outgoing.requestFocus();
                }
        } // close SendButtonListener

        public class IncomingReader implements Runnable {
                public void run() {
                        String message;
                        try {
                                while ((message = reader.readLine()) != null) {
                                        System.out.println("read " + message);
                                        incoming.append(message + "\n" + "\n");
                                } // close while
                        } catch (IOException ex) {
                                ex.printStackTrace();
                        }
                } // close run
        } // close IncomingReader
} // close outer class