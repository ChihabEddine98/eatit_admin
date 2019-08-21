package com.chihab_eddine98.eatit_admin.model;

public class Food {


    private String nom;
    private String imgUrl;
    private String description;
    private String prix;
    private String reduction;
    private String categoryId;


    public Food() {
    }


    public Food(String nom, String imgUrl, String description, String prix, String reduction, String categoryId) {
        this.nom = nom;
        this.imgUrl = imgUrl;
        this.description = description;
        this.prix = prix;
        this.reduction = reduction;
        this.categoryId = categoryId;
    }


    public String getNom() {
        return nom;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPrix() {
        return prix;
    }

    public String getReduction() {
        return reduction;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
