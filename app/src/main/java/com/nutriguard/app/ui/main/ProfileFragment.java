package com.nutriguard.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.nutriguard.app.R;
import com.nutriguard.app.data.model.Allergen;
import com.nutriguard.app.data.preferences.UserPreferences;
import com.nutriguard.app.ui.adapter.AllergenAdapter;
import java.util.List;

/**
 * Health Profile Fragment: lets user configure food allergies and intolerance alerts.
 */
public class ProfileFragment extends Fragment {

    private TextView badgeActiveCount;
    private MaterialButton btnResetAllergens;
    private RecyclerView recyclerAllergens;

    private UserPreferences userPrefs;
    private AllergenAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPrefs = new UserPreferences(requireContext());

        badgeActiveCount = view.findViewById(R.id.badgeActiveCount);
        btnResetAllergens = view.findViewById(R.id.btnResetAllergens);
        recyclerAllergens = view.findViewById(R.id.recyclerAllergens);

        recyclerAllergens.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Allergen> defaultAllergens = Allergen.getDefaultAllergens();
        adapter = new AllergenAdapter(defaultAllergens, userPrefs, (allergen, isChecked) -> {
            updateBadgeCount();
        });
        recyclerAllergens.setAdapter(adapter);

        btnResetAllergens.setOnClickListener(v -> {
            userPrefs.clearAll();
            adapter.notifyDataSetChanged();
            updateBadgeCount();
            Toast.makeText(requireContext(), "Toutes les restrictions ont été réinitialisées.", Toast.LENGTH_SHORT).show();
        });

        updateBadgeCount();
    }

    private void updateBadgeCount() {
        int count = userPrefs.getActiveAllergenCount();
        if (count > 0) {
            badgeActiveCount.setText(count + " actif(s)");
            badgeActiveCount.setVisibility(View.VISIBLE);
        } else {
            badgeActiveCount.setText("Aucun actif");
            badgeActiveCount.setVisibility(View.VISIBLE);
        }
    }
}
