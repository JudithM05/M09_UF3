import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream in;
    private String nomClient;
    private static final String MSG_SORTIR = "sortir";
    
    public FilServidorXat(ObjectInputStream in, String nomClient) {
        this.in = in;
        this.nomClient = nomClient;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            do {
                missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);
            } while (!missatge.equals(MSG_SORTIR));
            System.out.println("Fil de xat finalitzat.");
        } catch (Exception e) {
            System.out.println("Error en el fil de lectura: " + e.getMessage());
        }
    }
}