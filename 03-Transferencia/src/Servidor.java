import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public Socket connectar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
            System.out.println("Esperant connexio...");
            Socket socket = serverSocket.accept();
            System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
            return socket;
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
            return null;
        }
    }

    public void tancarConnexio(Socket socket) {
        try {
            if (socket != null) {
                System.out.println("Tancant connexió amb el client: " + socket.getRemoteSocketAddress());
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            System.out.println("Error tancant la connexió: " + e.getMessage());
        }
    }

    public void enviarFitxers(Socket socket) {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) in.readObject();
            System.out.println("Nomfitxer rebut: " + nomFitxer);

            if (nomFitxer == null || nomFitxer.isEmpty()) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            File fitxer = new File(nomFitxer);
            if (fitxer.exists()) {
                byte[] contingut = Files.readAllBytes(Paths.get(nomFitxer));
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                out.writeObject(contingut);
                out.flush();
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            } else {
                System.out.println("Error: El fitxer no existeix");
                out.writeObject(null);
            }
        } catch (Exception e) {
            System.out.println("Error llegint el fitxer del client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = servidor.connectar();

        if (socket != null) {
            servidor.enviarFitxers(socket);
            servidor.tancarConnexio(socket);
        }
    }
}