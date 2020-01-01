package com.dimitriongoua.libus.model;

import io.realm.RealmObject;

@SuppressWarnings("unused")
public class LibusButton extends RealmObject {

    private String libelle;
    private String ussd;
    private int activation;
    @SuppressWarnings("FieldCanBeLocal")
    private String created;


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

    public void newActivation() {
        this.activation++;
    }
}
