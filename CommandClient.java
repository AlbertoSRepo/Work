import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private static final List<String> availableConnections = new ArrayList<>();

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            // Avvia il thread per la lettura dei messaggi dal server
            new Thread(new ServerListener(in)).start();

            while (true) {
                System.out.println("Inserisci un comando ('connect', 'send', 'quit', 'close', 'list'): ");
                String command = scanner.nextLine();

                if ("connect".equalsIgnoreCase(command)) {
                    System.out.print("Inserisci l'indirizzo IP del server finale: ");
                    String ipAddress = scanner.nextLine();
                    System.out.print("Inserisci la porta del server finale: ");
                    int port = scanner.nextInt();
                    scanner.nextLine(); // Consuma il newline rimanente
                    out.println(command);
                    out.println(ipAddress);
                    out.println(port);
                } else if ("list".equalsIgnoreCase(command)) {
                    out.println(command);
                } else if ("send".equalsIgnoreCase(command)) {
                    System.out.println("Connessioni disponibili:");
                    for (int i = 0; i < availableConnections.size(); i++) {
                        System.out.println((i + 1) + ": " + availableConnections.get(i));
                    }
                    System.out.print("Seleziona l'indice della connessione: ");
                    int connectionIndex = scanner.nextInt();
                    scanner.nextLine(); // Consuma il newline rimanente

                    if (connectionIndex > 0 && connectionIndex <= availableConnections.size()) {
                        String selectedConnection = availableConnections.get(connectionIndex - 1);
                        System.out.print("Inserisci il messaggio: ");
                        String message = scanner.nextLine();
                        out.println(command);
                        out.println(selectedConnection);
                        out.println(message);
                    } else {
                        System.out.println("Indice non valido.");
                    }
                } else if ("quit".equalsIgnoreCase(command)) {
                    System.out.println("Connessioni disponibili:");
                    for (int i = 0; i < availableConnections.size(); i++) {
                        System.out.println((i + 1) + ": " + availableConnections.get(i));
                    }
                    System.out.print("Seleziona l'indice della connessione: ");
                    int connectionIndex = scanner.nextInt();
                    scanner.nextLine(); // Consuma il newline rimanente

                    if (connectionIndex > 0 && connectionIndex <= availableConnections.size()) {
                        String selectedConnection = availableConnections.get(connectionIndex - 1);
                        out.println(command);
                        out.println(selectedConnection);
                    } else {
                        System.out.println("Indice non valido.");
                    }
                } else if ("close".equalsIgnoreCase(command)) {
                    out.println(command);
                }
            }
        } catch (Exception e) {
            System.out.println("Errore durante l'invio dei comandi: " + e.getMessage());
        }
    }

    private static class ServerListener implements Runnable {
        private final BufferedReader in;

        ServerListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("Risposta dal router: " + response);
                    if (response.startsWith("Connessione stabilita con il server finale ")) {
                        availableConnections.add(response.substring("Connessione stabilita con il server finale ".length()));
                    } else if (response.startsWith("Connessione chiusa con il server finale ")) {
                        availableConnections.remove(response.substring("Connessione chiusa con il server finale ".length()));
                    }
                }
            } catch (Exception e) {
                System.out.println("Errore durante la lettura dalla connessione: " + e.getMessage());
            }
        }
    }
}