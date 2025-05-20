import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            while (!sortir) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                GestorClients gc = new GestorClients(clientSocket, this);
                gc.start();
            }
        } catch (IOException e) {
            System.out.println("Error al escoltar: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error tancant el servidor.");
        }
    }

    public synchronized void finalitzarXat() {
        System.out.println("Tancant tots els clients.");
        enviarMissatgeGrup(MSG_SORTIR);
        System.out.println("DEBUG: multicast " + MSG_SORTIR);
        clients.clear();
        sortir = true;
        pararServidor();
    }

    public synchronized void afegirClient(GestorClients client) {
        clients.put(client.getNom(), client);
        System.out.println(client.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + client.getNom());
        enviarMissatgeGrup("Entra: " + client.getNom());
    }

    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            enviarMissatgeGrup("Surt: " + nom);
        }
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        String msg = Missatge.getMissatgeGrup(missatge);
        for (GestorClients c : clients.values()) {
            c.enviarMissatge("Servidor", msg);
        }
    }

    public synchronized void enviarMissatgePersonal(String dest, String remitent, String missatge) {
        GestorClients c = clients.get(dest);
        if (c != null) {
            // Enviem al client amb format adequat
            String msg = Missatge.CODI_MSG_PERSONAL + "#" + remitent + "#" + missatge;
            c.enviarMissatge(remitent, msg);
            System.out.println("Missatge personal per (" + dest + ") de (" + remitent + "): " + missatge);
        } else {
            System.out.println("El client destinatari no existeix.");
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        servidor.pararServidor();
    }
}