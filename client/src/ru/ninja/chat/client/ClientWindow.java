package ru.ninja.chat.client;

import ru.ninja.network.TCPConnection;
import ru.ninja.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 8189;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientWindow());
    }

    private TCPConnection connection;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("alex");
    private final JTextField inputMessage = new JTextField();


    private ClientWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        inputMessage.addActionListener(this);
        add(inputMessage, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);
        setVisible(true);

        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMsg("Connecting exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = inputMessage.getText();
        if (msg.equals("")) return;
        inputMessage.setText(null);
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection Ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connecting closed");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connecting exception: " + e);
    }

    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
