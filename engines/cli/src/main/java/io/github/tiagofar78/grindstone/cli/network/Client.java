package io.github.tiagofar78.grindstone.cli.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println(serverIn.readLine());
                } catch (IOException e) {
                    System.out.println("Could not receive server message.");
                    e.printStackTrace();
                }
            }
        }).start();
        
        PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String message = keyboard.readLine();
            serverOut.println(message);
            if (message.equals("exit")) {
                break;
            }
        }
        
        socket.close();
    }

}
