import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;

    public void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor");
        }
    }

    public void pararServidor() {
        try {
            serverSocket.close();
            System.out.println("Servidor aturat");
        } catch (Exception e) {
            System.out.println("Error aturant el servidor");
        }
    }

    public String getNom(ObjectInputStream in) {
        try {
            String nom = (String) in.readObject();
            System.out.println("Nom rebut: " + nom);
            return nom;
        } catch (Exception e) {
            System.out.println("Error al rebre el nom");
            return "Desconegut";
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.iniciarServidor();
        
        try {
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
            
            // Crear streams
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Enviar petició de nom
            out.writeObject("Escriu el teu nom: ");
            out.flush();
            
            // Obtenir nom
            String nomClient = servidor.getNom(in);
            
            // Crear i iniciar fil
            FilServidorXat filServidor = new FilServidorXat(in, nomClient);
            System.out.println("Fil de xat creat.");
            filServidor.start();
            System.out.println("Fil de " + nomClient + " iniciat");
            
            // Enviar missatges
            Scanner scanner = new Scanner(System.in);
            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                out.writeObject(missatge);
                out.flush();
            } while (!missatge.equals(MSG_SORTIR));
            
            // Esperar al fil
            filServidor.join();
            
            // Tancar socket
            clientSocket.close();
            scanner.close();
            servidor.pararServidor();
        } catch (Exception e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}