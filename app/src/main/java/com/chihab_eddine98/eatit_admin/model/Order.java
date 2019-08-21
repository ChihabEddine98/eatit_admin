package com.chihab_eddine98.eatit_admin.model;

import java.util.List;

public class Order
{
    private String phone;
    private String nom;
    private String adresse;
    private String total;
    private List<FoodOrder> foods;
    private String status;

    // Status : 0:En préparation  1:En route  2:Livrée  3:Remboursement

    public Order() {
    }

    public Order(String phone, String nom, String adresse, String total, List<FoodOrder> foods) {
        this.phone = phone;
        this.nom = nom;
        this.adresse = adresse;
        this.total = total;
        this.foods = foods;
        status="0";
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<FoodOrder> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodOrder> foods) {
        this.foods = foods;
    }
}
