package com.nutriguard.app.utils;

import com.nutriguard.app.data.model.Allergen;
import com.nutriguard.app.data.model.Product;
import com.nutriguard.app.data.preferences.UserPreferences;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Utility to cross-reference food ingredients & tags against the user's allergy profile.
 */
public class AllergenChecker {

    /**
     * Returns the list of allergens present in the product that conflict with user's restrictions.
     */
    public static List<Allergen> checkProductAllergens(Product product, UserPreferences userPrefs) {
        List<Allergen> detectedOffenses = new ArrayList<>();
        if (product == null || userPrefs == null) {
            return detectedOffenses;
        }

        Set<String> activeAllergenIds = userPrefs.getSelectedAllergens();
        if (activeAllergenIds.isEmpty()) {
            return detectedOffenses; // No user allergies configured
        }

        List<Allergen> allAllergens = Allergen.getDefaultAllergens();
        List<String> productTags = product.getAllergensTags();
        String rawAllergens = product.getAllergens() != null ? product.getAllergens().toLowerCase(Locale.FRENCH) : "";
        String ingredients = product.getIngredientsText() != null ? product.getIngredientsText().toLowerCase(Locale.FRENCH) : "";

        for (Allergen allergen : allAllergens) {
            if (activeAllergenIds.contains(allergen.getId())) {
                boolean found = false;

                // 1. Check official tags from OpenFoodFacts
                for (String tag : allergen.getTags()) {
                    for (String pTag : productTags) {
                        if (pTag != null && pTag.equalsIgnoreCase(tag)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }

                // 2. Check raw allergen text
                if (!found && !rawAllergens.isEmpty()) {
                    for (String kw : allergen.getFrenchKeywords()) {
                        if (rawAllergens.contains(kw)) {
                            found = true;
                            break;
                        }
                    }
                }

                // 3. Check ingredient text keywords
                if (!found && !ingredients.isEmpty()) {
                    for (String kw : allergen.getFrenchKeywords()) {
                        // Match whole word or ingredient segment
                        if (ingredients.contains(kw)) {
                            found = true;
                            break;
                        }
                    }
                }

                if (found) {
                    detectedOffenses.add(allergen);
                }
            }
        }

        return detectedOffenses;
    }
}
