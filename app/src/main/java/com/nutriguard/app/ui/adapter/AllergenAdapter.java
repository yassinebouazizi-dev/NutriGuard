package com.nutriguard.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.nutriguard.app.R;
import com.nutriguard.app.data.model.Allergen;
import com.nutriguard.app.data.preferences.UserPreferences;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for user allergen restrictions checklist in ProfileFragment.
 */
public class AllergenAdapter extends RecyclerView.Adapter<AllergenAdapter.AllergenViewHolder> {

    public interface OnAllergenChangeListener {
        void onAllergenToggled(Allergen allergen, boolean isChecked);
    }

    private final List<Allergen> allergenList = new ArrayList<>();
    private final UserPreferences userPrefs;
    private final OnAllergenChangeListener listener;

    public AllergenAdapter(List<Allergen> list, UserPreferences userPrefs, OnAllergenChangeListener listener) {
        if (list != null) {
            this.allergenList.addAll(list);
        }
        this.userPrefs = userPrefs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllergenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allergen_checkbox, parent, false);
        return new AllergenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllergenViewHolder holder, int position) {
        Allergen allergen = allergenList.get(position);
        holder.bind(allergen, userPrefs, listener);
    }

    @Override
    public int getItemCount() {
        return allergenList.size();
    }

    static class AllergenViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtKeywords;
        private final MaterialCheckBox checkBox;

        public AllergenViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtAllergenName);
            txtKeywords = itemView.findViewById(R.id.txtAllergenKeywords);
            checkBox = itemView.findViewById(R.id.checkboxAllergen);
        }

        public void bind(final Allergen allergen, final UserPreferences userPrefs, final OnAllergenChangeListener listener) {
            txtName.setText(allergen.getName());

            // Build keyword preview
            StringBuilder sb = new StringBuilder("Ex : ");
            List<String> kw = allergen.getFrenchKeywords();
            for (int i = 0; i < Math.min(kw.size(), 4); i++) {
                sb.append(kw.get(i));
                if (i < Math.min(kw.size(), 4) - 1) {
                    sb.append(", ");
                }
            }
            txtKeywords.setText(sb.toString());

            boolean isChecked = userPrefs.isAllergenEnabled(allergen.getId());
            checkBox.setChecked(isChecked);

            itemView.setOnClickListener(v -> {
                boolean newState = !checkBox.isChecked();
                checkBox.setChecked(newState);
                userPrefs.setAllergenEnabled(allergen.getId(), newState);
                if (listener != null) {
                    listener.onAllergenToggled(allergen, newState);
                }
            });
        }
    }
}
