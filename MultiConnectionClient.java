import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MultiConnectionClient {
    private final Map<String, Connection> connections = new HashMap<>();

    public static void main(String[] args) {
        new MultiConnectionClient().run();
    }

    private void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Comando ('connect', 'send', 'quit'): ");
                String command = scanner.nextLine();

                if ("quit".equalsIgnoreCase(command)) {
                    break;
                } else if ("connect".equalsIgnoreCase(command)) {
                    System.out.print("Inserire l'indirizzo IP: ");
                    String ipAddress = scanner.nextLine();
                    if (!connections.containsKey(ipAddress)) {
                        try {
                            Connection conn = new Connection(ipAddress, 1234);
                            connections.put(ipAddress, conn);
                            new Thread(conn).start();
                        } catch (Exception e) {
                            System.out.println("Errore durante la creazione della connessione: " + e.getMessage());
                        }
                    }
                } else if ("send".equalsIgnoreCase(command)) {
                    System.out.print("Inserire l'indirizzo IP: ");
                    String ipAddress = scanner.nextLine();
                    Connection conn = connections.get(ipAddress);
                    if (conn != null) {
                        System.out.print("Inserire il messaggio: ");
                        String message = scanner.nextLine();
                        conn.sendMessage(message);
                    } else {
                        System.out.println("Connessione non trovata.");
                    }
                }
            }
        }
    }

    private static class Connection implements Runnable {
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;

        Connection(String ip, int port) throws Exception {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("Risposta dal server: " + response);
                }
            } catch (Exception e) {
                System.out.println("Errore durante la lettura dalla connessione: " + e.getMessage());
            }
        }

        void sendMessage(String message) {
            out.println(message);
        }
    }
}
