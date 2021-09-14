package com.example.capstoneproject.Model;

public class FoodCategory {
    private String foodCatName;
    private String foodImageURL;

    public FoodCategory() {

    }

    public FoodCategory(String foodCatName, String foodImageURL) {
        this.foodCatName = foodCatName;
        this.foodImageURL = foodImageURL;
    }

    public String getFoodCatName() {
        return foodCatName;
    }

    public String getFoodImageURL() {
        return foodImageURL;
    }

    public void setFoodCatName(String foodCatName) {
        this.foodCatName = foodCatName;
    }

    public void setFoodImageURL(String foodImageURL) {
        this.foodImageURL = foodImageURL;
    }
}
