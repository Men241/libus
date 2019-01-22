package co.dimitriongoua.libus.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Libelle extends RealmObject {

    private String libelle;
    private String ussd;
    private int activation;
    private String created;


    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

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
