package com.chihab_eddine98.eatit_admin.model;

public class FoodOrder {


    private String foodId;
    private String foodName;
    private String qte;
    private String prix;
    private String reduction;

    public FoodOrder() {
    }

    public FoodOrder(String foodId, String foodName, String qte, String prix, String reduction) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.qte = qte;
        this.prix = prix;
        this.reduction = reduction;
    }


    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getQte() {
        return qte;
    }

    public void setQte(String qte) {
        this.qte = qte;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getReduction() {
        return reduction;
    }

    public void setReduction(String reduction) {
        this.reduction = reduction;
    }
}
