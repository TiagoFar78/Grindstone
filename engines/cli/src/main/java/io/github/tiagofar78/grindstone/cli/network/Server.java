package io.github.tiagofar78.grindstone.cli.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import io.github.tiagofar78.grindstone.cli.CommandLineInterface;

public class Server {
    
    private static final int MAX_CLIENTS = 10;
    
    private static CommandLineInterface cli;
    private static ServerSocket server;
    private static Socket[] IDS_TAKEN = new Socket[MAX_CLIENTS];
    private static PrintWriter[] OUTS = new PrintWriter[MAX_CLIENTS];
    
    private static int assignId(Socket socket) throws IOException {
        for (int i = 0; i < MAX_CLIENTS; i++) {
            if (IDS_TAKEN[i] == null) {
                IDS_TAKEN[i] = socket;
                OUTS[i] = new PrintWriter(socket.getOutputStream(), true);
                return i;
            }
        }
        
        return -1;
    }
    
    private static void connectClient(Socket client) throws IOException {
        int id = assignId(client);
        if (id == -1) {
            System.out.println("Client tried to connect but server is full");
            try {
                new PrintWriter(client.getOutputStream(), true).println("Server is full");
            } catch (IOException e) {
                // Empty
            }
        }

        System.out.println("New client connected");
        sendMessage(id, "Connected to server");
        new Thread(() -> handleClient(id, client)).start();
    }
    
    private static void disconnectClient(int id) {
        cli.disconnect(id);
        IDS_TAKEN[id] = null;
        OUTS[id] = null;
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println("Server started");
        server = new ServerSocket(12345);
        cli = new CommandLineInterface();

        while (true) {
            connectClient(server.accept());
        }
    }

    static void handleClient(int id, Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] args = line.trim().split("\\s+");
                if (args[0].equals("close")) {
                    server.close();
                    break;
                }
                else if (args[0].equals("exit")) {
                    disconnectClient(id);
                    continue;
                }
                
                cli.process(id, args);
            }
        } catch (IOException e) {
            disconnectClient(id);
        }
    }
    
    public static void sendMessage(int id, String message) {
        OUTS[id].println(message);
    }

}
