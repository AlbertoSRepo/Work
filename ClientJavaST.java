import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientJavaST {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Inserire l'indirizzo IP: ");
            String ipAddress = scanner.nextLine();

            try (Socket socket = new Socket(ipAddress, 1234);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String inputLine;
                while (true) {
                    System.out.print("Inserire il testo da inviare (digita 'quit' per terminare): ");
                    inputLine = scanner.nextLine();
                    if ("quit".equalsIgnoreCase(inputLine)) {
                        break;
                    }
                    out.println(inputLine);
                    String response = in.readLine();
                    System.out.println("Risposta dal server: " + response);
                }
            } catch (Exception e) {
                System.out.println("Impossibile stabilire la connessione: " + e.getMessage());
            }
        }
    }
}
