 /**
 * P2PChat.java - Simple Peer-to-Peer Chat Application
 * 
 * A lightweight P2P chat application using Java Sockets and Swing for GUI.
 * This application allows two users to communicate over a network without a central server.
 * 
 * Features:
 * - Peer-to-Peer messaging (1-to-1)
 * - Uses Sockets for real-time communication
 * - Multi-threaded (Handles multiple users)
 * - Simple Swing GUI for chat
 * 
 * Developed by Naol
 * Copyright © 2024 Naol. All rights reserved.
 */

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class P2PChat extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public P2PChat(boolean isServer) {
        setTitle(isServer ? "P2P Chat - Server" : "P2P Chat - Client");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        new Thread(() -> startChat(isServer)).start();

        setVisible(true);
    }

    private void startChat(boolean isServer) {
        try {
            if (isServer) {
                serverSocket = new ServerSocket(5000);
                appendMessage("Waiting for connection...");
                socket = serverSocket.accept();
                appendMessage("Client connected!");
            } else {
                socket = new Socket("localhost", 5000);
                appendMessage("Connected to server!");
            }

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = reader.readLine()) != null) {
                appendMessage("Friend: " + message);
            }
        } catch (IOException e) {
            appendMessage("Connection closed.");
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            appendMessage("You: " + message);
            writer.println(message);
            messageField.setText("");
        }
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        boolean isServer = JOptionPane.showConfirmDialog(null, "Run as server?", "P2P Chat", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        new P2PChat(isServer);
    }
}
