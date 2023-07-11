package JavaEchoServers;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServerChar {
    public static void main(String[] args) {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     Reader in = new InputStreamReader(clientSocket.getInputStream())) {

                    System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress() + ":" + clientSocket.getPort());
                    int data;
                    while ((data = in.read()) != -1) {
                        char dataChar = (char) data;
                        System.out.print(dataChar);
                        out.print(dataChar);
                        out.flush();
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
