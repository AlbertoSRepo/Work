import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ESConnectionRouter {
    private static final int COMMAND_PORT = 4321;

    private final Map<String, Connection> connections = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        new ESConnectionRouter().run();
    }

    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(COMMAND_PORT)) {
            System.out.println("Server di comando in ascolto sulla porta " + COMMAND_PORT);

            while (true) {
                Socket commandSocket = serverSocket.accept();
                executorService.execute(new CommandHandler(commandSocket));
            }
        } catch (Exception e) {
            System.out.println("Errore del server di comando: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    private class CommandHandler implements Runnable {
        private final Socket commandSocket;

        CommandHandler(Socket commandSocket) {
            this.commandSocket = commandSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
                PrintWriter out = new PrintWriter(commandSocket.getOutputStream(), true)
            ) {
                System.out.println("Connessione stabilita con il client " + commandSocket.getRemoteSocketAddress());

                String command = in.readLine();
                String[] parts = command.split(" ", 2);

                if ("quit".equalsIgnoreCase(parts[0])) {
                    String ipAddress = in.readLine();
                    int port = Integer.parseInt(in.readLine());
                    String key = ipAddress + ":" + port;
                    Connection conn = connections.get(key);
                    if (conn != null) {
                        conn.closeConnection();
                        connections.remove(key);
                        System.out.println("Connessione chiusa con il server finale " + key);
                    } else {
                        System.out.println("Connessione non trovata.");
                    }
                } else if ("close".equalsIgnoreCase(parts[0])) {
                    for (Connection conn : connections.values()) {
                        conn.closeConnection();
                    }
                    connections.clear();
                    System.out.println("Tutte le connessioni sono state chiuse.");
                } else if ("connect".equalsIgnoreCase(parts[0])) {
                    String ipAddress = in.readLine();
                    int port = Integer.parseInt(in.readLine());
                    String key = ipAddress + ":" + port;
                    if (!connections.containsKey(key)) {
                        try {
                            Connection conn = new Connection(ipAddress, port);
                            connections.put(key, conn);
                            executorService.execute(conn);
                            System.out.println("Connessione stabilita con il server finale " + key);
                        } catch (Exception e) {
                            System.out.println("Errore durante la creazione della connessione: " + e.getMessage());
                        }
                    }
                } else if ("send".equalsIgnoreCase(parts[0])) {
                    String ipAddress = in.readLine();
                    int port = Integer.parseInt(in.readLine());
                    String key = ipAddress + ":" + port;
                    Connection conn = connections.get(key);
                    if (conn != null) {
                        String message = in.readLine();
                        conn.sendMessage(message);
                        System.out.println("Messaggio inviato al server finale " + key + ": " + message);
                    } else {
                        System.out.println("Connessione non trovata.");
                    }
                }
                out.println(""); // Send an empty line to signal the end of the response.
            } catch (Exception e) {
                System.out.println("Errore del server di comando: " + e.getMessage());
            } finally {
                try {
                    commandSocket.close();
                } catch (Exception e) {
                    System.out.println("Errore durante la chiusura della connessione: " + e.getMessage());
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
                    System.out.println("Risposta dal server finale: " + response);
                }
            } catch (Exception e) {
                System.out.println("Errore durante la lettura dalla connessione: " + e.getMessage());
            }
        }

        void sendMessage(String message) {
            out.println(message);
        }

        void closeConnection() {
            out.println("QUIT");
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
}
