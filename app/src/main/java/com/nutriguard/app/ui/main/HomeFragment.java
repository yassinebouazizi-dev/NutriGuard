package com.nutriguard.app.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nutriguard.app.R;
import com.nutriguard.app.data.api.OpenFoodFactsApiService;
import com.nutriguard.app.data.api.RetrofitClient;
import com.nutriguard.app.data.database.ProductDao;
import com.nutriguard.app.data.model.Product;
import com.nutriguard.app.data.model.ProductResponse;
import com.nutriguard.app.data.model.SearchResponse;
import com.nutriguard.app.data.preferences.UserPreferences;
import com.nutriguard.app.ui.adapter.ProductAdapter;
import com.nutriguard.app.ui.detail.ProductDetailActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Home & Explorer Fragment with modern medical UI and Camera Barcode Scanner.
 */
public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private ImageView btnHeaderCamera;
    private EditText editSearchQuery;
    private ImageView btnBarcodeSearch;
    private ImageView btnCameraScan;
    private MaterialCardView cardHeroScanner;
    private RecyclerView recyclerProducts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutLoading;
    private LinearLayout layoutError;
    private LinearLayout layoutEmpty;
    private TextView txtErrorMessage;
    private MaterialButton btnRetry;

    private ProductAdapter adapter;
    private ProductDao productDao;
    private UserPreferences userPrefs;
    private OpenFoodFactsApiService apiService;

    private String currentQuery = "bio";
    private Call<?> currentCall;

    // ZXing Camera Barcode Scanner Launcher
    private final ActivityResultLauncher<ScanOptions> barcodeScannerLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result != null && result.getContents() != null) {
                    String scannedCode = result.getContents();
                    editSearchQuery.setText(scannedCode);
                    fetchBarcode(scannedCode);
                }
            }
    );

    // Camera runtime permission launcher
    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startCameraScan();
                } else {
                    Toast.makeText(requireContext(), R.string.camera_permission_required, Toast.LENGTH_LONG).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPrefs = new UserPreferences(requireContext());
        productDao = new ProductDao(requireContext());
        apiService = RetrofitClient.getApiService();

        // Bind Views
        btnHeaderCamera = view.findViewById(R.id.btnHeaderCamera);
        editSearchQuery = view.findViewById(R.id.editSearchQuery);
        btnBarcodeSearch = view.findViewById(R.id.btnBarcodeSearch);
        btnCameraScan = view.findViewById(R.id.btnCameraScan);
        cardHeroScanner = view.findViewById(R.id.cardHeroScanner);
        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        txtErrorMessage = view.findViewById(R.id.txtErrorMessage);
        btnRetry = view.findViewById(R.id.btnRetry);

        // Setup RecyclerView
        recyclerProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProductAdapter(this, userPrefs);
        recyclerProducts.setAdapter(adapter);

        // Camera Scanner Listeners
        View.OnClickListener cameraClickListener = v -> checkCameraAndScan();
        btnCameraScan.setOnClickListener(cameraClickListener);
        cardHeroScanner.setOnClickListener(cameraClickListener);
        if (btnHeaderCamera != null) {
            btnHeaderCamera.setOnClickListener(cameraClickListener);
        }

        // Search text listeners
        editSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(editSearchQuery.getText().toString().trim());
                return true;
            }
            return false;
        });

        btnBarcodeSearch.setOnClickListener(v -> {
            String query = editSearchQuery.getText().toString().trim();
            if (!query.isEmpty() && query.matches("\\d+")) {
                fetchBarcode(query);
            } else {
                Toast.makeText(requireContext(), "Entrez un code-barres numérique ou utilisez la caméra", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup Demo Barcode Chips & Categories
        setupDemoBarcodeChips(view);
        setupCategoryChips(view);

        // Swipe to Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> performSearch(currentQuery));

        // Retry button
        btnRetry.setOnClickListener(v -> performSearch(currentQuery));

        // Initial default load
        performSearch(currentQuery);
    }

    private void checkCameraAndScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraScan();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCameraScan() {
        ScanOptions options = new ScanOptions();
        options.setPrompt(getString(R.string.zxing_prompt));
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivity.class);
        barcodeScannerLauncher.launch(options);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupDemoBarcodeChips(View view) {
        Chip chipNutella = view.findViewById(R.id.chipNutella);
        Chip chipCoca = view.findViewById(R.id.chipCoca);
        Chip chipBarilla = view.findViewById(R.id.chipBarilla);
        Chip chipEvian = view.findViewById(R.id.chipEvian);
        Chip chipPrince = view.findViewById(R.id.chipPrince);

        chipNutella.setOnClickListener(v -> fetchBarcode("3017620422003"));
        chipCoca.setOnClickListener(v -> fetchBarcode("5449000000996"));
        chipBarilla.setOnClickListener(v -> fetchBarcode("8076809513753"));
        chipEvian.setOnClickListener(v -> fetchBarcode("3068320114454"));
        chipPrince.setOnClickListener(v -> fetchBarcode("7622210449283"));
    }

    private void setupCategoryChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupCategories);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipCatAll) {
                performSearch("aliment");
            } else if (id == R.id.chipCatBeverages) {
                performCategorySearch("boissons");
            } else if (id == R.id.chipCatSnacks) {
                performCategorySearch("snacks");
            } else if (id == R.id.chipCatCereals) {
                performCategorySearch("cereales");
            } else if (id == R.id.chipCatDairy) {
                performCategorySearch("produits-laitiers");
            } else if (id == R.id.chipCatBiscuits) {
                performCategorySearch("biscuits");
            }
        });
    }

    public void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            query = "bio";
        }
        currentQuery = query;

        if (query.matches("\\d{8,14}")) {
            fetchBarcode(query);
            return;
        }

        showLoading();

        if (currentCall != null) {
            currentCall.cancel();
        }

        Call<SearchResponse> call = apiService.searchProducts(query, 1, "process", 1, 24, 1);
        currentCall = call;

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    if (products == null || products.isEmpty()) {
                        showEmpty();
                    } else {
                        showResults(products);
                    }
                } else {
                    showError("Réponse serveur inattendue (" + response.code() + ").");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                hideLoading();
                showError("Vérifiez votre connexion Internet.\n" + t.getLocalizedMessage());
            }
        });
    }

    private void performCategorySearch(String category) {
        showLoading();
        if (currentCall != null) {
            currentCall.cancel();
        }

        Call<SearchResponse> call = apiService.getProductsByCategory(category, 24);
        currentCall = call;

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getProducts();
                    if (products == null || products.isEmpty()) {
                        showEmpty();
                    } else {
                        showResults(products);
                    }
                } else {
                    showError("Erreur lors de la récupération de la catégorie.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                hideLoading();
                showError("Impossible de charger les données.\n" + t.getLocalizedMessage());
            }
        });
    }

    public void fetchBarcode(String barcode) {
        showLoading();
        if (currentCall != null) {
            currentCall.cancel();
        }

        Call<ProductResponse> call = apiService.getProductByBarcode(barcode);
        currentCall = call;

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Product product = response.body().getProduct();
                    if (product.getCode().isEmpty()) {
                        product.setCode(barcode);
                    }
                    List<Product> list = new ArrayList<>();
                    list.add(product);
                    showResults(list);
                    onProductClick(product);
                } else {
                    showEmpty();
                    Toast.makeText(requireContext(), "Produit non trouvé pour le code " + barcode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                if (call.isCanceled()) return;
                hideLoading();
                showError("Erreur réseau lors de la recherche du code-barres.");
            }
        });
    }

    private void showLoading() {
        swipeRefreshLayout.setRefreshing(false);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        recyclerProducts.setVisibility(View.GONE);
    }

    private void hideLoading() {
        layoutLoading.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showResults(List<Product> products) {
        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        recyclerProducts.setVisibility(View.VISIBLE);
        adapter.setProducts(products);
    }

    private void showEmpty() {
        recyclerProducts.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        recyclerProducts.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        txtErrorMessage.setText(message);
    }

    @Override
    public void onProductClick(Product product) {
        productDao.addToHistory(product);
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
