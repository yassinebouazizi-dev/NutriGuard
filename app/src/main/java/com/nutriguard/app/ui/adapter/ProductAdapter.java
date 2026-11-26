package com.nutriguard.app.ui.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nutriguard.app.R;
import com.nutriguard.app.data.model.Allergen;
import com.nutriguard.app.data.model.Product;
import com.nutriguard.app.data.preferences.UserPreferences;
import com.nutriguard.app.utils.AllergenChecker;
import com.nutriguard.app.utils.NutriScoreHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying food product cards.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;
    private final UserPreferences userPrefs;

    public ProductAdapter(OnProductClickListener listener, UserPreferences userPrefs) {
        this.listener = listener;
        this.userPrefs = userPrefs;
    }

    public void setProducts(List<Product> newProducts) {
        this.products.clear();
        if (newProducts != null) {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener, userPrefs);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgThumb;
        private final TextView txtName;
        private final TextView txtBrand;
        private final TextView txtAllergenAlert;
        private final TextView badgeNutriScore;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgProductThumb);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtBrand = itemView.findViewById(R.id.txtProductBrand);
            txtAllergenAlert = itemView.findViewById(R.id.txtAllergenAlert);
            badgeNutriScore = itemView.findViewById(R.id.badgeNutriScore);
        }

        public void bind(final Product product, final OnProductClickListener listener, final UserPreferences userPrefs) {
            Context context = itemView.getContext();

            txtName.setText(product.getNameOrFallback());

            String brandInfo = product.getBrandOrFallback();
            if (product.getQuantity() != null && !product.getQuantity().isEmpty()) {
                brandInfo += " • " + product.getQuantity();
            }
            txtBrand.setText(brandInfo);

            // Load product thumbnail
            String imageUrl = product.getBestImageUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_nutriguard_logo)
                    .error(R.drawable.ic_nutriguard_logo)
                    .into(imgThumb);

            // Nutri-Score badge
            String grade = product.getNutriscoreGrade();
            if (grade != null && !grade.isEmpty() && !grade.equalsIgnoreCase("unknown")) {
                badgeNutriScore.setText(grade.toUpperCase(Locale.ROOT));
                int color = NutriScoreHelper.getNutriScoreColor(context, grade);
                GradientDrawable bg = (GradientDrawable) badgeNutriScore.getBackground().mutate();
                bg.setColor(color);
                badgeNutriScore.setVisibility(View.VISIBLE);
            } else {
                badgeNutriScore.setText("?");
                int color = NutriScoreHelper.getNutriScoreColor(context, null);
                GradientDrawable bg = (GradientDrawable) badgeNutriScore.getBackground().mutate();
                bg.setColor(color);
                badgeNutriScore.setVisibility(View.VISIBLE);
            }

            // Real-time Allergen conflict check
            List<Allergen> detected = AllergenChecker.checkProductAllergens(product, userPrefs);
            if (!detected.isEmpty()) {
                StringBuilder sb = new StringBuilder("Contient : ");
                for (int i = 0; i < detected.size(); i++) {
                    sb.append(detected.get(i).getName());
                    if (i < detected.size() - 1) {
                        sb.append(", ");
                    }
                }
                txtAllergenAlert.setText(sb.toString());
                txtAllergenAlert.setVisibility(View.VISIBLE);
            } else {
                txtAllergenAlert.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
