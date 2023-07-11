import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RouterVicat {
    private static final int SERVER_PORT = 8080;

    private static final Map<String, Socket> connections = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'avvio del server: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String ipAddress = in.readLine();
                while (ipAddress != null) {
                    int port = Integer.parseInt(in.readLine());
                    System.out.println("Richiesta di connessione da: " + ipAddress + ":" + port);

                    Socket echoServerSocket = connections.get(ipAddress);
                    if (echoServerSocket == null) {
                        echoServerSocket = new Socket(ipAddress, port);
                        connections.put(ipAddress, echoServerSocket);
                        System.out.println("Nuova connessione stabilita con il server finale " + ipAddress + ":" + port);

                        // Handshake con il server finale
                        PrintWriter echoServerOut = new PrintWriter(echoServerSocket.getOutputStream(), true);
                        BufferedReader echoServerIn = new BufferedReader(new InputStreamReader(echoServerSocket.getInputStream()));
                        echoServerOut.println("Handshake");

                        // Ricevi la risposta dal server finale
                        String handshakeResponse = echoServerIn.readLine();
                        System.out.println("Risposta dal server finale: " + handshakeResponse);
                    }

                    out.println("Connessione stabilita con il server finale " + ipAddress + ":" + port);

                    // Leggi il prossimo indirizzo IP
                    ipAddress = in.readLine();
                }
            } catch (Exception e) {
                System.out.println("Errore durante la gestione del client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    System.out.println("Errore nella chiusura del socket del client: " + e.getMessage());
                }
            }
        }
    }
}
