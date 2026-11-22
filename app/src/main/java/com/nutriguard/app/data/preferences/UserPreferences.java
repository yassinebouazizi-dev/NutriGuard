package com.nutriguard.app.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages user allergen settings and preferences using SharedPreferences.
 */
public class UserPreferences {

    private static final String PREF_NAME = "nutriguard_user_prefs";
    private static final String KEY_ALLERGENS = "selected_allergens";

    private final SharedPreferences prefs;

    public UserPreferences(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized Set<String> getSelectedAllergens() {
        return new HashSet<>(prefs.getStringSet(KEY_ALLERGENS, new HashSet<>()));
    }

    public synchronized void setAllergenEnabled(String allergenId, boolean enabled) {
        Set<String> current = getSelectedAllergens();
        if (enabled) {
            current.add(allergenId);
        } else {
            current.remove(allergenId);
        }
        prefs.edit().putStringSet(KEY_ALLERGENS, current).apply();
    }

    public synchronized boolean isAllergenEnabled(String allergenId) {
        return getSelectedAllergens().contains(allergenId);
    }

    public synchronized int getActiveAllergenCount() {
        return getSelectedAllergens().size();
    }

    public synchronized void clearAll() {
        prefs.edit().remove(KEY_ALLERGENS).apply();
    }
}
