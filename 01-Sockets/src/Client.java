import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static final int PORT = 7777;
    static final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.out.println("Error al connectar al servidor");
        }
    }

    public void envia(String missatge) {
        if (out != null) {
            out.println(missatge);
            System.out.println("Enviat al servidor: " + missatge);
        }
    }

    public void tanca() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat");
        } catch (Exception e) {
            System.out.println("Error al tancar el client");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.conecta();

        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adéu!");

        System.out.println("Prem Enter per tancar el client...");
        new Scanner(System.in).nextLine();

        client.tanca();
    }
}
