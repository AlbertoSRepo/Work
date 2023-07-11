package JavaEchoServers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServerLine extends Thread {
    private int port;

    public EchoServerLine(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress() + ":" + clientSocket.getPort());
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Received from client: " + inputLine);
                        out.println(inputLine);
                    }
                } catch (Exception e) {
                    System.out.println("Connection interrupted: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
