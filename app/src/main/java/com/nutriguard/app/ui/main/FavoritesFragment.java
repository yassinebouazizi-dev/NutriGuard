package com.nutriguard.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.nutriguard.app.R;
import com.nutriguard.app.data.database.ProductDao;
import com.nutriguard.app.data.model.Product;
import com.nutriguard.app.data.preferences.UserPreferences;
import com.nutriguard.app.ui.adapter.ProductAdapter;
import com.nutriguard.app.ui.detail.ProductDetailActivity;
import java.util.List;

/**
 * Fragment managing Offline Bookmarks (Favorites) and Recently Scanned Products (History).
 */
public class FavoritesFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private MaterialButtonToggleGroup toggleGroup;
    private TextView txtCountLabel;
    private TextView btnClearHistory;
    private RecyclerView recyclerFavorites;
    private LinearLayout layoutEmptyState;
    private TextView iconEmpty;
    private TextView txtEmptyTitle;
    private TextView txtEmptySubtitle;

    private ProductDao productDao;
    private UserPreferences userPrefs;
    private ProductAdapter adapter;

    private boolean isHistoryMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productDao = new ProductDao(requireContext());
        userPrefs = new UserPreferences(requireContext());

        toggleGroup = view.findViewById(R.id.toggleGroupFavorites);
        txtCountLabel = view.findViewById(R.id.txtCountLabel);
        btnClearHistory = view.findViewById(R.id.btnClearHistory);
        recyclerFavorites = view.findViewById(R.id.recyclerFavorites);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        iconEmpty = view.findViewById(R.id.iconEmpty);
        txtEmptyTitle = view.findViewById(R.id.txtEmptyTitle);
        txtEmptySubtitle = view.findViewById(R.id.txtEmptySubtitle);

        recyclerFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProductAdapter(this, userPrefs);
        recyclerFavorites.setAdapter(adapter);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            isHistoryMode = (checkedId == R.id.btnTabHistory);
            loadData();
        });

        btnClearHistory.setOnClickListener(v -> {
            productDao.clearHistory();
            Toast.makeText(requireContext(), R.string.history_cleared, Toast.LENGTH_SHORT).show();
            loadData();
        });

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Product> list;
        if (isHistoryMode) {
            list = productDao.getAllHistory();
            btnClearHistory.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
            txtCountLabel.setText(list.size() + " produit(s) consulté(s)");

            iconEmpty.setText("🕒");
            txtEmptyTitle.setText(R.string.empty_history_title);
            txtEmptySubtitle.setText(R.string.empty_history_subtitle);
        } else {
            list = productDao.getAllFavorites();
            btnClearHistory.setVisibility(View.GONE);
            txtCountLabel.setText(list.size() + " favori(s) enregistré(s)");

            iconEmpty.setText("💚");
            txtEmptyTitle.setText(R.string.empty_favorites_title);
            txtEmptySubtitle.setText(R.string.empty_favorites_subtitle);
        }

        if (list.isEmpty()) {
            recyclerFavorites.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerFavorites.setVisibility(View.VISIBLE);
            adapter.setProducts(list);
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
