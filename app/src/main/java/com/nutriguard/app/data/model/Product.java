package com.nutriguard.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Model representing a food product from Open Food Facts.
 */
public class Product implements Serializable {

    @SerializedName("code")
    private String code;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("brands")
    private String brands;

    @SerializedName("quantity")
    private String quantity;

    @SerializedName("categories")
    private String categories;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("image_front_url")
    private String imageFrontUrl;

    @SerializedName("image_front_small_url")
    private String imageFrontSmallUrl;

    @SerializedName("ingredients_text")
    private String ingredientsText;

    @SerializedName("allergens")
    private String allergens;

    @SerializedName("allergens_tags")
    private List<String> allergensTags;

    @SerializedName("nutriscore_grade")
    private String nutriscoreGrade;

    @SerializedName("nova_group")
    private Integer novaGroup;

    @SerializedName("ecoscore_grade")
    private String ecoscoreGrade;

    @SerializedName("nutriments")
    private Nutriments nutriments;

    // Getters and Setters
    public String getCode() {
        return code != null ? code : "";
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFrontUrl() {
        return imageFrontUrl;
    }

    public void setImageFrontUrl(String imageFrontUrl) {
        this.imageFrontUrl = imageFrontUrl;
    }

    public String getImageFrontSmallUrl() {
        return imageFrontSmallUrl;
    }

    public void setImageFrontSmallUrl(String imageFrontSmallUrl) {
        this.imageFrontSmallUrl = imageFrontSmallUrl;
    }

    public String getIngredientsText() {
        return ingredientsText;
    }

    public void setIngredientsText(String ingredientsText) {
        this.ingredientsText = ingredientsText;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public List<String> getAllergensTags() {
        return allergensTags != null ? allergensTags : new ArrayList<>();
    }

    public void setAllergensTags(List<String> allergensTags) {
        this.allergensTags = allergensTags;
    }

    public String getNutriscoreGrade() {
        return nutriscoreGrade != null ? nutriscoreGrade.toLowerCase(Locale.ROOT) : "";
    }

    public void setNutriscoreGrade(String nutriscoreGrade) {
        this.nutriscoreGrade = nutriscoreGrade;
    }

    public Integer getNovaGroup() {
        return novaGroup;
    }

    public void setNovaGroup(Integer novaGroup) {
        this.novaGroup = novaGroup;
    }

    public String getEcoscoreGrade() {
        return ecoscoreGrade != null ? ecoscoreGrade.toLowerCase(Locale.ROOT) : "";
    }

    public void setEcoscoreGrade(String ecoscoreGrade) {
        this.ecoscoreGrade = ecoscoreGrade;
    }

    public Nutriments getNutriments() {
        return nutriments;
    }

    public void setNutriments(Nutriments nutriments) {
        this.nutriments = nutriments;
    }

    // Helper methods for presentation
    public String getNameOrFallback() {
        if (productName != null && !productName.trim().isEmpty()) {
            return productName.trim();
        }
        return "Produit sans nom";
    }

    public String getBrandOrFallback() {
        if (brands != null && !brands.trim().isEmpty()) {
            return brands.trim();
        }
        return "Marque non précisée";
    }

    public String getBestImageUrl() {
        if (imageFrontUrl != null && !imageFrontUrl.isEmpty()) {
            return imageFrontUrl;
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (imageFrontSmallUrl != null && !imageFrontSmallUrl.isEmpty()) {
            return imageFrontSmallUrl;
        }
        return null;
    }
}
