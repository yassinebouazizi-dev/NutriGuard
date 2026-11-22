package com.nutriguard.app.data.api;

import com.nutriguard.app.data.model.ProductResponse;
import com.nutriguard.app.data.model.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit REST Interface for Open Food Facts API.
 */
public interface OpenFoodFactsApiService {

    /**
     * Fetch full product information by barcode.
     * Example: https://world.openfoodfacts.org/api/v2/product/3017620422003.json
     */
    @GET("api/v2/product/{barcode}.json")
    Call<ProductResponse> getProductByBarcode(@Path("barcode") String barcode);

    /**
     * Search products by keywords.
     * Example: https://world.openfoodfacts.org/cgi/search.pl?search_terms=nutella&search_simple=1&action=process&json=1&page_size=25&page=1
     */
    @GET("cgi/search.pl")
    Call<SearchResponse> searchProducts(
            @Query("search_terms") String searchTerms,
            @Query("search_simple") int searchSimple,
            @Query("action") String action,
            @Query("json") int json,
            @Query("page_size") int pageSize,
            @Query("page") int page
    );

    /**
     * Fetch products by category.
     */
    @GET("category/{category}.json")
    Call<SearchResponse> getProductsByCategory(
            @Path("category") String category,
            @Query("page_size") int pageSize
    );
}
