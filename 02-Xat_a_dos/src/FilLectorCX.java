import java.io.ObjectInputStream;

public class FilLectorCX extends Thread {
    private ObjectInputStream in;
    
    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while (true) {
                missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);
            }
        } catch (Exception e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}