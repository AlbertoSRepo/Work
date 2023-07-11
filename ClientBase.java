import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientBase {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.print("Inserisci l'indirizzo IP del server finale: ");
            String ipAddress = scanner.nextLine();
            System.out.print("Inserisci la porta del server finale: ");
            int port = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline rimanente
            
            out.println(ipAddress);
            out.println(port);

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Risposta dal server finale: " + response);

                // Invio dati al server finale
                System.out.print("Inserisci i dati da inviare al server finale (exit per uscire): ");
                String data = scanner.nextLine();
                if ("exit".equalsIgnoreCase(data)) {
                    break;
                }

                out.println(data);

                // Ricezione risposta dal server finale
                String serverResponse = in.readLine();
                System.out.println("Risposta dal server finale: " + serverResponse);
            }
        } catch (Exception e) {
            System.out.println("Errore durante la connessione al server: " + e.getMessage());
        }
    }
}
