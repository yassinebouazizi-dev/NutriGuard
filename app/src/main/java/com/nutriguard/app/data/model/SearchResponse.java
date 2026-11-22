package com.nutriguard.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * API response for multi-product search request.
 */
public class SearchResponse {

    @SerializedName("count")
    private int count;

    @SerializedName("page")
    private int page;

    @SerializedName("products")
    private List<Product> products;

    public int getCount() {
        return count;
    }

    public int getPage() {
        return page;
    }

    public List<Product> getProducts() {
        return products != null ? products : new ArrayList<>();
    }
}
