package com.chihab_eddine98.eatit_admin.model;

public class Category {

    private String nom;
    private String imgUrl;


    public Category(String nom, String imgUrl) {
        this.nom = nom;
        this.imgUrl = imgUrl;
    }


    public Category() {
    }


    public String getNom() {
        return nom;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
