package com.nutriguard.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * API response for a single product request.
 */
public class ProductResponse {

    @SerializedName("code")
    private String code;

    @SerializedName("status")
    private int status;

    @SerializedName("status_verbose")
    private String statusVerbose;

    @SerializedName("product")
    private Product product;

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusVerbose() {
        return statusVerbose;
    }

    public Product getProduct() {
        return product;
    }

    public boolean isSuccess() {
        return status == 1 && product != null;
    }
}
