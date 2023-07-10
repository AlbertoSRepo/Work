package JavaEchoServers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer3 {
    public static void main(String[] args) {
        int port = 3333;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server in ascolto sulla porta " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("Connessione accettata da " + clientSocket.getRemoteSocketAddress());
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Ricevuto dal client: " + inputLine);
                        out.println(inputLine);
                    }
                } catch (Exception e) {
                    System.out.println("Connessione interrotta: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Errore del server: " + e.getMessage());
        }
    }
}
