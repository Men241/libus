package com.dimitriongoua.libus.model;

import io.realm.RealmObject;

@SuppressWarnings("unused")
public class LibusButton extends RealmObject {

    private String libelle;
    private String ussd;
    private int activation;
    @SuppressWarnings("FieldCanBeLocal")
    private String created;

    public LibusButton() {
    }

    public LibusButton(String libelle, String ussd) {
        super();
        this.libelle = libelle;
        this.ussd = ussd;
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

    public void newActivation() {
        this.activation++;
    }
}
