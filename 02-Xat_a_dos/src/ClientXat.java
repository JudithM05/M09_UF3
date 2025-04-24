import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final String MSG_SORTIR = "sortir";
    
    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            
            // Crear streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (Exception e) {
            System.out.println("Error al connectar: " + e.getMessage());
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            out.writeObject(missatge);
            out.flush();
            System.out.println("Enviant missatge: " + missatge);
        } catch (Exception e) {
            System.out.println("Error al enviar missatge: " + e.getMessage());
        }
    }
    
    public void tancarClient() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat.");
        } catch (Exception e) {
            System.out.println("Error al tancar el client: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        
        try {
            // Crear i iniciar fil de lectura
            FilLectorCX filLector = new FilLectorCX(client.in);
            filLector.start();
            
            // Enviar missatges
            Scanner scanner = new Scanner(System.in);
            String missatge;
            System.out.print("Missatge ('sortir' per tancar): ");
            do {
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
            } while (!missatge.equals(MSG_SORTIR));
            
            // Tancar recursos
            scanner.close();
            client.tancarClient();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}