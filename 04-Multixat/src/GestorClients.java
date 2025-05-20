import java.io.*;
import java.net.*;

public class GestorClients extends Thread {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) {
        this.client = client;
        this.servidor = servidor;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creant fluxos: " + e.getMessage());
        }
    }

    public String getNom() {
        return nom;
    }

    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        } finally {
            if (nom != null) {
                servidor.eliminarClient(nom);
            }
            try {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                if (client != null) client.close();
            } catch (IOException e) {
                System.out.println("Error tancant el socket del client.");
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            System.out.println("Error enviant missatge a " + remitent);
        }
    }

    public void processaMissatge(String missatgeRaw) {
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null || parts == null) {
            System.out.println("Missatge desconegut.");
            return;
        }

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length >= 2) {
                    this.nom = parts[1];
                    servidor.afegirClient(this);
                }
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(this.nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    String dest = parts[1];
                    String miss = parts[2];
                    servidor.enviarMissatgePersonal(dest, this.nom, miss);
                }
                break;
            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    servidor.enviarMissatgeGrup(this.nom + ": " + parts[1]);
                }
                break;
            default:
                System.out.println("Missatge desconegut: " + codi);
        }
    }
}