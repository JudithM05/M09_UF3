import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    static final int PORT = 7777;
    static final String HOST = "localhost";
    private static ServerSocket srvSocket;
    private static Socket clientSocket;

    public void connecta() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
            System.out.println("Esperant connexions a " + HOST + ":" + PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor");
        }
    }

    public void repDades() {
        try {
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(clientSocket.getInputStream()));
            String s;
            while ((s = in.readLine()) != null) {
                System.out.println("Rebut: " + s);
                if (s.equalsIgnoreCase("Adéu!") || s.equalsIgnoreCase("Adeu!")) {
                    break;
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println("Error llegint del client");
        }
    }

    public void tanca() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (srvSocket != null) srvSocket.close();
            System.out.println("Servidor tancat.");
        } catch (Exception e) {
            System.out.println("Error tancant el servidor");
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connecta();
        servidor.repDades();
        servidor.tanca();
    }
}
