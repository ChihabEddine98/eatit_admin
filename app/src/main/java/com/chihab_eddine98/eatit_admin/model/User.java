package com.chihab_eddine98.eatit_admin.model;

public class User
{

    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private String mdp;
    // Ajouté pour le besoin
    private String phone;
    // Admin difference
    private String isStuff;


    public User(String nom, String prenom, String dateDeNaissance, String mdp, String phone, String isStuff) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.mdp = mdp;
        this.phone = phone;
        this.isStuff = isStuff;
    }

    public User(String nom, String prenom, String dateDeNaissance, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateDeNaissance = dateDeNaissance;
        this.mdp = mdp;
    }

    public User() {
    }

    public String getMdp() {
        return mdp;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNomComplet() {
        return affiche(nom)+" "+affiche(prenom);
    }

    public String affiche(String word)
    {
        return word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase();

    }

    public String getIsStuff() {
        return isStuff;
    }

    public void setIsStuff(String isStuff) {
        this.isStuff = isStuff;
    }

    public String getDateDeNaissance() {
        return dateDeNaissance;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
