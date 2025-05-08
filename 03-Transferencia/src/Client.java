import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final String DIR_ARRIBADA = "/tmp";
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connectant a -> " + HOST + ":" + PORT);
            System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
        } catch (Exception e) {
            System.out.println("Error al connectar: " + e.getMessage());
        }
    }

    public void rebreFitxers() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                String nomFitxer = scanner.nextLine();
                
                if (nomFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Sortint...");
                    break;
                }
                
                out.writeObject(nomFitxer);
                out.flush();
                
                System.out.print("Nom del fitxer a guardar: ");
                String desti = scanner.nextLine();
                if (desti.isEmpty()) {
                    desti = DIR_ARRIBADA + "/" + new File(nomFitxer).getName();
                }
                
                byte[] contingut = (byte[]) in.readObject();
                
                if (contingut != null) {
                    Files.write(Paths.get(desti), contingut);
                    System.out.println("Fitxer rebut i guardat com: " + desti);
                } else {
                    System.out.println("Error: No s'ha pogut rebre el fitxer");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al rebre el fitxer: " + e.getMessage());
        }
    }

    public void tancarConnexio() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) {
                socket.close();
                System.out.println("Connexio tancada.");
            }
        } catch (Exception e) {
            System.out.println("Error tancant la connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connecta();
        
        if (client.socket != null) {
            client.rebreFitxers();
            client.tancarConnexio();
        }
    }
}