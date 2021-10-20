package com.mitchell;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatAppServer {

    ArrayList<PrintWriter> clientOutputStreams;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket) {
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // close ClientHandler constructor

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("server read:" + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // close run
    } // close Client Handler

    public static void main (String[] args) {
        new ChatAppServer().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList<PrintWriter>(); // generify to PrintWriter?
        try {
            ServerSocket serverSock = new ServerSocket(43210);

            while(true) {
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("connected to client");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } // close go

    public void tellEveryone(String message) {

        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // end while
    }
}