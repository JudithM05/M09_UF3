public class Fitxer {
    private String nom;
    private byte[] contingut;
    
    public Fitxer(String nom, byte[] contingut) {
        this.nom = nom;
        this.contingut = contingut;
    }

    public String getNom() {
        return nom;
    }

    public byte[] getContingut() {
        return contingut;
    }
}
