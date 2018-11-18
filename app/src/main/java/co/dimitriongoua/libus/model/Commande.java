package co.dimitriongoua.libus.model;

import io.realm.RealmObject;

public class Commande extends RealmObject {

    private String libelle;
    private String ussd;
    private int activation;

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getUssd() {
        return ussd;
    }

    public void setUssd(String ussd) {
        this.ussd = ussd;
    }

    public int getActivation() {
        return activation;
    }

    public void setActivation(int activation) {
        this.activation = activation;
    }
}
