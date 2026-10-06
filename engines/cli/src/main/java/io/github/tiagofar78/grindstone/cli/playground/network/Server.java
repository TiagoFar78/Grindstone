package io.github.tiagofar78.grindstone.cli.playground.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import io.github.tiagofar78.grindstone.cli.playground.Playground;

public class Server {
    
    private static final int MAX_CLIENTS = 10;
    
    public static final Playground playground = new Playground();
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
                PrintWriter printer = new PrintWriter(client.getOutputStream(), true);
                printer.println("Server is full");
                printer.close();
            } catch (IOException e) {
                // Empty
            }
            return;
        }

        System.out.println("New client connected");
        sendMessage(id, "Connected to server");
        playground.connect(id);
        new Thread(() -> handleClient(id, client)).start();
    }
    
    private static void disconnectClient(int id) {
        playground.disconnect(id);
        IDS_TAKEN[id] = null;
        OUTS[id] = null;
        System.out.println("Client disconnected");
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println("Server started");
        server = new ServerSocket(12345);

        while (true) {
            connectClient(server.accept());
        }
    }

    static void handleClient(int id, Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] args = line.trim().split("\\s+");
                if (args[0].equals("close")) {
                    server.close();
                    break;
                }
                else if (args[0].equals("exit")) {
                    disconnectClient(id);
                    continue;
                }
                
                playground.process(id, args);
            }
        } catch (IOException e) {
            disconnectClient(id);
        }
    }
    
    public static void sendMessage(int id, String message) {
        OUTS[id].println(message);
    }

}
