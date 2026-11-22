package com.nutriguard.app.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.nutriguard.app.R;

/**
 * Helper utility for Nutri-Score, Nova Group, and Eco-Score formatting and colors.
 */
public class NutriScoreHelper {

    public static int getNutriScoreColor(Context context, String grade) {
        if (grade == null) return ContextCompat.getColor(context, R.color.nutriscore_unknown);
        switch (grade.toLowerCase().trim()) {
            case "a": return ContextCompat.getColor(context, R.color.nutriscore_a);
            case "b": return ContextCompat.getColor(context, R.color.nutriscore_b);
            case "c": return ContextCompat.getColor(context, R.color.nutriscore_c);
            case "d": return ContextCompat.getColor(context, R.color.nutriscore_d);
            case "e": return ContextCompat.getColor(context, R.color.nutriscore_e);
            default: return ContextCompat.getColor(context, R.color.nutriscore_unknown);
        }
    }

    public static int getNovaColor(Context context, Integer group) {
        if (group == null) return ContextCompat.getColor(context, R.color.nova_unknown);
        switch (group) {
            case 1: return ContextCompat.getColor(context, R.color.nova_1);
            case 2: return ContextCompat.getColor(context, R.color.nova_2);
            case 3: return ContextCompat.getColor(context, R.color.nova_3);
            case 4: return ContextCompat.getColor(context, R.color.nova_4);
            default: return ContextCompat.getColor(context, R.color.nova_unknown);
        }
    }

    public static int getEcoScoreColor(Context context, String grade) {
        if (grade == null) return ContextCompat.getColor(context, R.color.ecoscore_unknown);
        switch (grade.toLowerCase().trim()) {
            case "a": return ContextCompat.getColor(context, R.color.ecoscore_a);
            case "b": return ContextCompat.getColor(context, R.color.ecoscore_b);
            case "c": return ContextCompat.getColor(context, R.color.ecoscore_c);
            case "d": return ContextCompat.getColor(context, R.color.ecoscore_d);
            case "e": return ContextCompat.getColor(context, R.color.ecoscore_e);
            default: return ContextCompat.getColor(context, R.color.ecoscore_unknown);
        }
    }

    public static String getNovaDescription(Context context, Integer group) {
        if (group == null) return context.getString(R.string.score_not_available);
        switch (group) {
            case 1: return context.getString(R.string.nova_desc_1);
            case 2: return context.getString(R.string.nova_desc_2);
            case 3: return context.getString(R.string.nova_desc_3);
            case 4: return context.getString(R.string.nova_desc_4);
            default: return context.getString(R.string.score_not_available);
        }
    }
}
