package com.nutriguard.app.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allergen definition for user health profile & detection.
 */
public class Allergen implements Serializable {

    private final String id;
    private final String name;
    private final List<String> tags;
    private final List<String> frenchKeywords;

    public Allergen(String id, String name, List<String> tags, List<String> frenchKeywords) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.frenchKeywords = frenchKeywords;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getFrenchKeywords() {
        return frenchKeywords;
    }

    /**
     * Predefined list of standard allergens as monitored in the European Union (INCO regulation).
     */
    public static List<Allergen> getDefaultAllergens() {
        List<Allergen> list = new ArrayList<>();

        list.add(new Allergen("gluten", "Gluten / Blé",
                Arrays.asList("en:gluten", "en:wheat", "en:barley", "en:rye", "en:oats"),
                Arrays.asList("gluten", "blé", "orge", "seigle", "avoine", "épeautre", "froment")));

        list.add(new Allergen("milk", "Lait / Lactose",
                Arrays.asList("en:milk", "en:dairy", "en:lactose"),
                Arrays.asList("lait", "lactose", "beurre", "crème", "fromage", "lactosérum", "caséinate")));

        list.add(new Allergen("peanuts", "Arachides / Cacahuètes",
                Arrays.asList("en:peanuts", "en:peanut"),
                Arrays.asList("arachide", "arachides", "cacahuète", "cacahuètes")));

        list.add(new Allergen("nuts", "Fruits à coque (Noisettes, Noix, Amandes)",
                Arrays.asList("en:nuts", "en:hazelnuts", "en:almonds", "en:walnuts", "en:cashews"),
                Arrays.asList("noisette", "noisettes", "amande", "amandes", "noix", "cajou", "pistache", "pécan")));

        list.add(new Allergen("eggs", "Œufs",
                Arrays.asList("en:eggs", "en:egg"),
                Arrays.asList("œuf", "oeuf", "œufs", "oeufs", "albumine", "jaune d'oeuf", "blanc d'oeuf")));

        list.add(new Allergen("soybeans", "Soja",
                Arrays.asList("en:soybeans", "en:soy"),
                Arrays.asList("soja", "lécithine de soja", "protéine de soja")));

        list.add(new Allergen("fish", "Poissons",
                Arrays.asList("en:fish"),
                Arrays.asList("poisson", "poissons", "saumon", "thon", "morue", "cabillaud")));

        list.add(new Allergen("crustaceans", "Crustacés & Mollusques",
                Arrays.asList("en:crustaceans", "en:molluscs"),
                Arrays.asList("crustacé", "crustacés", "crevette", "crabe", "homard", "mollusque", "moule")));

        list.add(new Allergen("mustard", "Moutarde",
                Arrays.asList("en:mustard"),
                Arrays.asList("moutarde", "graine de moutarde")));

        list.add(new Allergen("sesame", "Graines de sésame",
                Arrays.asList("en:sesame-seeds", "en:sesame"),
                Arrays.asList("sésame", "sesame")));

        list.add(new Allergen("celery", "Céleri",
                Arrays.asList("en:celery"),
                Arrays.asList("céleri", "celeri")));

        list.add(new Allergen("palmoil", "Huile de palme (Régime & Écologie)",
                Arrays.asList("en:palm-oil", "en:palm-fat"),
                Arrays.asList("huile de palme", "graisse de palme", "palme", "palmiste")));

        return list;
    }
}
