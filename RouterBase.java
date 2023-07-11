import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RouterBase {
    private static final int SERVER_PORT = 8080;

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
                    String ipAddress = inputLine;
                    int port = Integer.parseInt(in.readLine());
                    System.out.println("Stabilendo connessione con: " + ipAddress + ":" + port); // Aggiunto
                    Socket echoServerSocket = new Socket(ipAddress, port);
                    out.println("Connessione stabilita con il server finale " + ipAddress + ":" + port);

                    // Inoltre, in caso di connessione stabilita, si può inviare direttamente il messaggio
                    String message = in.readLine();
                    System.out.println("Invio il messaggio '" + message + "' alla connessione: " + ipAddress + ":" + port); // Aggiunto
                    PrintWriter echoServerOut = new PrintWriter(echoServerSocket.getOutputStream(), true);
                    echoServerOut.println(message);

                    BufferedReader echoServerIn = new BufferedReader(new InputStreamReader(echoServerSocket.getInputStream()));
                    out.println("Risposta dal server finale: " + echoServerIn.readLine());

                    echoServerSocket.close();
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
