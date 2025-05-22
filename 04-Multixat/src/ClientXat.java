// =======================
// ClientXat.java (actualitzat)
// =======================
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            System.out.println("Client connectat a localhost:9999");
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.out.println("No s'ha pogut connectar amb el servidor.");
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            out.writeObject(missatge);
            out.flush();
            System.out.println("Enviant missatge: " + missatge);
        } catch (IOException e) {
            if (out == null) {
                System.out.println("oos null. Sortint...");
            } else {
                System.out.println("Error enviant missatge.");
            }
        }
    }

    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (in != null) {
                System.out.println("Flux d'entrada tancat.");
                in.close();
            }
            if (out != null) {
                System.out.println("Flux de sortida tancat.");
                out.close();
            }
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error tancant client.");
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String input;
        do {
            System.out.print(missatge);
            input = sc.nextLine().trim();
        } while (obligatori && input.isEmpty());
        return input;
    }

    public void executar() {
        Scanner sc = new Scanner(System.in);
        ajuda();

        Thread lector = new Thread(() -> {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                while (!sortir) {
                    String missatge = (String) in.readObject();
                    String codi = Missatge.getCodiMissatge(missatge);
                    String[] parts = Missatge.getPartsMissatge(missatge);

                    if (codi == null || parts == null) continue;

                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length >= 3) {
                                System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                            }
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length >= 2) {
                                System.out.println(parts[1]);
                            }
                            break;
                        default:
                            System.out.println("Missatge desconegut.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error rebent missatge. Sortint...");
            } finally {
                tancarClient();
            }
        });

        lector.start();

        while (!sortir) {
            String opcio = sc.nextLine().trim();
            if (opcio.isEmpty() || opcio.equals("4")) {
                sortir = true;
                enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                break;
            }

            switch (opcio) {
                case "1":
                    String nom = getLinea(sc, "Introdueix el nom: ", true);
                    enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    String dest = getLinea(sc, "Destinatari:: ", true);
                    String msg = getLinea(sc, "Missatge a enviar: ", true);
                    enviarMissatge(Missatge.getMissatgePersonal(dest, msg));
                    break;
                case "3":
                    String grupMsg = getLinea(sc, "Missatge grup: ", true);
                    enviarMissatge(Missatge.getMissatgeGrup(grupMsg));
                    break;
                case "5":
                    enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    sortir = true;
                    break;
                default:
                    System.out.println("Opció no vàlida.");
            }

            if (!sortir) ajuda();
        }

        try {
            lector.join();
        } catch (InterruptedException e) {
            // Ignorat
        }

        sc.close();
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        client.executar();
    }
} // fi de classe
