package com.nutriguard.app.ui.detail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.nutriguard.app.R;
import com.nutriguard.app.data.database.ProductDao;
import com.nutriguard.app.data.model.Allergen;
import com.nutriguard.app.data.model.Nutriments;
import com.nutriguard.app.data.model.Product;
import com.nutriguard.app.data.preferences.UserPreferences;
import com.nutriguard.app.utils.AllergenChecker;
import com.nutriguard.app.utils.NutriScoreHelper;
import java.util.List;
import java.util.Locale;

/**
 * Product Detail Activity: Comprehensive nutrition radar, dynamic allergen alerts, and offline bookmarking.
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";

    private Product product;
    private ProductDao productDao;
    private UserPreferences userPrefs;
    private boolean isFav = false;

    private ImageView detailProductImage;
    private TextView detailProductName;
    private TextView detailProductBrand;

    // Allergen Banner
    private MaterialCardView cardAllergenBanner;
    private ImageView imgAllergenIcon;
    private TextView txtAllergenBannerTitle;
    private TextView txtAllergenBannerSubtitle;

    // Health Indices
    private TextView badgeDetailNutriScore;
    private TextView badgeDetailNova;
    private TextView badgeDetailEcoScore;
    private TextView txtNovaDescription;

    // Nutrition & Ingredients
    private LinearLayout containerNutrientRows;
    private TextView txtIngredientsContent;

    // Actions
    private ImageView btnShare;
    private ImageView btnFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productDao = new ProductDao(this);
        userPrefs = new UserPreferences(this);

        product = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        if (product == null) {
            Toast.makeText(this, "Produit introuvable.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        populateData();
        setupAllergenBanner();
        setupFavoriteAction();
        setupShareAction();
    }

    private void initViews() {
        detailProductImage = findViewById(R.id.detailProductImage);
        detailProductName = findViewById(R.id.detailProductName);
        detailProductBrand = findViewById(R.id.detailProductBrand);

        cardAllergenBanner = findViewById(R.id.cardAllergenBanner);
        imgAllergenIcon = findViewById(R.id.imgAllergenIcon);
        txtAllergenBannerTitle = findViewById(R.id.txtAllergenBannerTitle);
        txtAllergenBannerSubtitle = findViewById(R.id.txtAllergenBannerSubtitle);

        badgeDetailNutriScore = findViewById(R.id.badgeDetailNutriScore);
        badgeDetailNova = findViewById(R.id.badgeDetailNova);
        badgeDetailEcoScore = findViewById(R.id.badgeDetailEcoScore);
        txtNovaDescription = findViewById(R.id.txtNovaDescription);

        containerNutrientRows = findViewById(R.id.containerNutrientRows);
        txtIngredientsContent = findViewById(R.id.txtIngredientsContent);

        btnShare = findViewById(R.id.btnShare);
        btnFavorite = findViewById(R.id.btnFavorite);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.detailToolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateData() {
        // Name & Brand
        detailProductName.setText(product.getNameOrFallback());
        String brandText = product.getBrandOrFallback();
        if (product.getQuantity() != null && !product.getQuantity().isEmpty()) {
            brandText += " • " + product.getQuantity();
        }
        if (!product.getCode().isEmpty()) {
            brandText += " • " + product.getCode();
        }
        detailProductBrand.setText(brandText);

        // Product Image
        String imageUrl = product.getBestImageUrl();
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_nutriguard_logo)
                .error(R.drawable.ic_nutriguard_logo)
                .into(detailProductImage);

        // Nutri-Score
        String nutriGrade = product.getNutriscoreGrade();
        if (nutriGrade != null && !nutriGrade.isEmpty()) {
            badgeDetailNutriScore.setText(nutriGrade.toUpperCase(Locale.ROOT));
            int color = NutriScoreHelper.getNutriScoreColor(this, nutriGrade);
            GradientDrawable bg = (GradientDrawable) badgeDetailNutriScore.getBackground().mutate();
            bg.setColor(color);
        } else {
            badgeDetailNutriScore.setText("?");
        }

        // NOVA Group
        Integer nova = product.getNovaGroup();
        if (nova != null && nova > 0) {
            badgeDetailNova.setText(String.valueOf(nova));
            int novaColor = NutriScoreHelper.getNovaColor(this, nova);
            GradientDrawable bg = (GradientDrawable) badgeDetailNova.getBackground().mutate();
            bg.setColor(novaColor);
            txtNovaDescription.setText("Groupe NOVA " + nova + " : " + NutriScoreHelper.getNovaDescription(this, nova));
        } else {
            badgeDetailNova.setText("-");
            txtNovaDescription.setText(R.string.score_not_available);
        }

        // Eco-Score
        String ecoGrade = product.getEcoscoreGrade();
        if (ecoGrade != null && !ecoGrade.isEmpty()) {
            badgeDetailEcoScore.setText(ecoGrade.toUpperCase(Locale.ROOT));
            int ecoColor = NutriScoreHelper.getEcoScoreColor(this, ecoGrade);
            GradientDrawable bg = (GradientDrawable) badgeDetailEcoScore.getBackground().mutate();
            bg.setColor(ecoColor);
        } else {
            badgeDetailEcoScore.setText("-");
        }

        // Ingredients
        String ingredients = product.getIngredientsText();
        if (ingredients != null && !ingredients.trim().isEmpty()) {
            txtIngredientsContent.setText(ingredients.trim());
        } else {
            txtIngredientsContent.setText(R.string.no_ingredients);
        }

        // Nutrition Table
        populateNutritionTable();
    }

    private void setupAllergenBanner() {
        int configuredCount = userPrefs.getActiveAllergenCount();
        if (configuredCount == 0) {
            // Unconfigured profile
            cardAllergenBanner.setCardBackgroundColor(ContextCompat.getColor(this, R.color.alert_warning_bg));
            imgAllergenIcon.setImageResource(R.drawable.ic_warning);
            imgAllergenIcon.setColorFilter(ContextCompat.getColor(this, R.color.alert_warning_text));
            txtAllergenBannerTitle.setText(R.string.allergen_unconfigured_title);
            txtAllergenBannerTitle.setTextColor(ContextCompat.getColor(this, R.color.alert_warning_text));
            txtAllergenBannerSubtitle.setText(R.string.allergen_unconfigured_subtitle);
            txtAllergenBannerSubtitle.setTextColor(ContextCompat.getColor(this, R.color.alert_warning_text));
            return;
        }

        List<Allergen> detected = AllergenChecker.checkProductAllergens(product, userPrefs);
        if (!detected.isEmpty()) {
            // DANGER DETECTED
            cardAllergenBanner.setCardBackgroundColor(ContextCompat.getColor(this, R.color.alert_danger_bg));
            imgAllergenIcon.setImageResource(R.drawable.ic_warning);
            imgAllergenIcon.setColorFilter(ContextCompat.getColor(this, R.color.alert_danger_text));
            txtAllergenBannerTitle.setText(R.string.allergen_danger_title);
            txtAllergenBannerTitle.setTextColor(ContextCompat.getColor(this, R.color.alert_danger_text));

            StringBuilder sb = new StringBuilder("Contient vos allergènes : ");
            for (int i = 0; i < detected.size(); i++) {
                sb.append(detected.get(i).getName());
                if (i < detected.size() - 1) {
                    sb.append(", ");
                }
            }
            txtAllergenBannerSubtitle.setText(sb.toString());
            txtAllergenBannerSubtitle.setTextColor(ContextCompat.getColor(this, R.color.alert_danger_text));
        } else {
            // SAFE
            cardAllergenBanner.setCardBackgroundColor(ContextCompat.getColor(this, R.color.alert_safe_bg));
            imgAllergenIcon.setImageResource(R.drawable.ic_check_circle);
            imgAllergenIcon.setColorFilter(ContextCompat.getColor(this, R.color.alert_safe_text));
            txtAllergenBannerTitle.setText(R.string.allergen_safe_title);
            txtAllergenBannerTitle.setTextColor(ContextCompat.getColor(this, R.color.alert_safe_text));
            txtAllergenBannerSubtitle.setText(R.string.allergen_safe_subtitle);
            txtAllergenBannerSubtitle.setTextColor(ContextCompat.getColor(this, R.color.alert_safe_text));
        }
    }

    private void populateNutritionTable() {
        containerNutrientRows.removeAllViews();
        Nutriments n = product.getNutriments();
        if (n == null) {
            addNutrientRow("Valeurs nutritionnelles", "Non communiquées");
            return;
        }

        addNutrientRow("Énergie (Calories)", n.formatValue(n.getEnergyKcal100g(), "kcal"));
        addNutrientRow("Matières grasses", n.formatValue(n.getFat100g(), "g"));
        addNutrientRow("  • dont acides gras saturés", n.formatValue(n.getSaturatedFat100g(), "g"));
        addNutrientRow("Glucides", n.formatValue(n.getCarbohydrates100g(), "g"));
        addNutrientRow("  • dont sucres", n.formatValue(n.getSugars100g(), "g"));
        addNutrientRow("Fibres alimentaires", n.formatValue(n.getFiber100g(), "g"));
        addNutrientRow("Protéines", n.formatValue(n.getProteins100g(), "g"));
        addNutrientRow("Sel", n.formatValue(n.getSalt100g(), "g"));
    }

    private void addNutrientRow(String label, String value) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_nutrient_row, containerNutrientRows, false);
        TextView txtLabel = row.findViewById(R.id.txtNutrientLabel);
        TextView txtValue = row.findViewById(R.id.txtNutrientValue);

        txtLabel.setText(label);
        txtValue.setText(value);
        containerNutrientRows.addView(row);
    }

    private void setupFavoriteAction() {
        isFav = productDao.isFavorite(product.getCode());
        updateFavoriteIcon();

        btnFavorite.setOnClickListener(v -> {
            if (isFav) {
                productDao.removeFavorite(product.getCode());
                isFav = false;
                Toast.makeText(this, R.string.fav_removed, Toast.LENGTH_SHORT).show();
            } else {
                productDao.addFavorite(product);
                isFav = true;
                Toast.makeText(this, R.string.fav_added, Toast.LENGTH_SHORT).show();
            }
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        if (isFav) {
            btnFavorite.setImageResource(R.drawable.ic_nav_favorites);
            btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.alert_danger_text));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_heart_border);
            btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    private void setupShareAction() {
        btnShare.setOnClickListener(v -> {
            String nutri = product.getNutriscoreGrade() != null ? product.getNutriscoreGrade().toUpperCase(Locale.ROOT) : "N/A";
            String shareBody = String.format(getString(R.string.share_product_text),
                    product.getNameOrFallback(),
                    product.getBrandOrFallback(),
                    nutri);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "Partager ce produit");
            startActivity(shareIntent);
        });
    }
}
