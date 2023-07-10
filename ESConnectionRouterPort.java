import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ESConnectionRouterPort {
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
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if ("connect".equalsIgnoreCase(inputLine)) {
                        String ipAddress = in.readLine();
                        int port = Integer.parseInt(in.readLine());
                        System.out.println("Stabilendo connessione con: " + ipAddress + ":" + port); // Aggiunto
                        Socket echoServerSocket = new Socket(ipAddress, port);
                        connections.put(ipAddress + ":" + port, echoServerSocket);
                        out.println("Connessione stabilita con il server finale " + ipAddress + ":" + port);
                    } else if ("list".equalsIgnoreCase(inputLine)) {
                        for (String connection : connections.keySet()) {
                            out.println(connection);
                        }
                    } else if ("send".equalsIgnoreCase(inputLine)) {
                        String connectionName = in.readLine();
                        String message = in.readLine();
                        System.out.println("Invio il messaggio '" + message + "' alla connessione: " + connectionName); // Aggiunto
                        Socket echoServerSocket = connections.get(connectionName);
                        if (echoServerSocket != null) {
                            PrintWriter echoServerOut = new PrintWriter(echoServerSocket.getOutputStream(), true);
                            echoServerOut.println(message);
                            BufferedReader echoServerIn = new BufferedReader(new InputStreamReader(echoServerSocket.getInputStream()));
                            out.println("Risposta dal server finale: " + echoServerIn.readLine());
                        }
                    } else if ("quit".equalsIgnoreCase(inputLine)) {
                        String connectionName = in.readLine();
                        System.out.println("Chiusura della connessione: " + connectionName); // Aggiunto
                        Socket echoServerSocket = connections.remove(connectionName);
                        if (echoServerSocket != null) {
                            echoServerSocket.close();
                            out.println("Connessione chiusa con il server finale " + connectionName);
                        }
                    } else if ("close".equalsIgnoreCase(inputLine)) {
                        System.out.println("Chiusura del client e delle connessioni."); // Aggiunto
                        clientSocket.close();
                        for (Socket echoServerSocket : connections.values()) {
                            echoServerSocket.close();
                        }
                        connections.clear();
                    }
                }
            } catch (Exception e) {
                System.out.println("Errore durante la gestione del client: " + e.getMessage());
            }
        }
    }
}
