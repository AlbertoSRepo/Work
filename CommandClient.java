import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 4321;

    private static final Map<String, Connection> openConnections = new HashMap<>();

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("Inserisci un comando ('connect', 'send', 'quit', 'close'): ");
                String command = scanner.nextLine();

                if ("connect".equalsIgnoreCase(command)) {
                    System.out.print("Inserisci l'indirizzo IP del server finale: ");
                    String ipAddress = scanner.nextLine();
                    System.out.print("Inserisci la porta del server finale: ");
                    int port = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    out.println(command);
                    out.println(ipAddress);
                    out.println(port);
                } else if ("send".equalsIgnoreCase(command)) {
                    System.out.println("Connessioni aperte:");
                    int index = 1;
                    for (String connection : openConnections.keySet()) {
                        System.out.println(index + ": " + connection);
                        index++;
                    }
                    System.out.print("Seleziona l'indice della connessione: ");
                    int connectionIndex = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    
                    if (connectionIndex > 0 && connectionIndex <= openConnections.size()) {
                        String[] connections = openConnections.keySet().toArray(new String[0]);
                        String selectedConnection = connections[connectionIndex - 1];
                        System.out.print("Inserisci il messaggio: ");
                        String message = scanner.nextLine();
                        out.println(command);
                        out.println(selectedConnection);
                        out.println(message);
                    } else {
                        System.out.println("Indice non valido.");
                    }
                } else if ("quit".equalsIgnoreCase(command)) {
                    System.out.println("Connessioni aperte:");
                    int index = 1;
                    for (String connection : openConnections.keySet()) {
                        System.out.println(index + ": " + connection);
                        index++;
                    }
                    System.out.print("Seleziona l'indice della connessione: ");
                    int connectionIndex = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over

                    if (connectionIndex > 0 && connectionIndex <= openConnections.size()) {
                        String[] connections = openConnections.keySet().toArray(new String[0]);
                        String selectedConnection = connections[connectionIndex - 1];
                        out.println(command);
                        out.println(selectedConnection);
                    } else {
                        System.out.println("Indice non valido.");
                    }
                } else if ("close".equalsIgnoreCase(command)) {
                    out.println(command);
                }

                String response;
                while (!(response = in.readLine()).isEmpty()) {
                    System.out.println("Risposta dal server: " + response);
                }
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'invio dei comandi: " + e.getMessage());
        }
    }
}
