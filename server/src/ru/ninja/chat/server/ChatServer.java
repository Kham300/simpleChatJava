package ru.ninja.chat.server;
import ru.ninja.network.TCPConnection;
import ru.ninja.network.TCPConnectionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener{

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("ServerRunning...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true){
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }

            }
        } catch (IOException e){
            throw  new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("client connected : " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("client disconnected : " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TcPConnection exception: " + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        for (TCPConnection tcpConnection : connections) tcpConnection.sendString(value);
    }
}
