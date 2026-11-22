# NutriGuard

Application mobile Android permettant de rechercher des produits alimentaires, de consulter leurs informations nutritionnelles (Nutri-Score, groupe NOVA, éco-score) et d'alerter l'utilisateur en cas de présence d'allergènes selon son profil.

Projet développé en **Java** et **XML** sous **Android Studio**.

## Fonctionnalités

- **Recherche de produits** : recherche textuelle ou par saisie de code-barres via l'API Open Food Facts.
- **Détection des allergènes** : configuration d'un profil personnalisé (gluten, lactose, arachides, fruits à coque, etc.) avec affichage d'un bandeau d'alerte sur la fiche produit en cas d'incompatibilité.
- **Indicateurs nutritionnels** : affichage du Nutri-Score, du groupe NOVA (degré de transformation de l'aliment) et de l'Éco-Score.
- **Tableau nutritionnel** : détail des valeurs nutritionnelles pour 100g (calories, sucres, matières grasses, sel, protéines, fibres).
- **Mode hors-ligne** : sauvegarde des produits favoris et de l'historique des consultations dans une base de données SQLite locale.

## Architecture et technologies

- **Langage** : Java
- **Interface utilisateur** : XML (ConstraintLayout, MaterialCardView, RecyclerView, BottomNavigationView)
- **Réseau** : Retrofit 2, OkHttp 3, Gson
- **Base de données locale** : SQLite (`SQLiteOpenHelper`)
- **Préférences utilisateur** : SharedPreferences
- **Chargement d'images** : Glide
- **API externe** : [Open Food Facts API](https://world.openfoodfacts.org/) (REST / JSON)

## Structure du projet

```
app/src/main/
├── java/com/nutriguard/app/
│   ├── data/
│   │   ├── api/            # Client Retrofit et interface OpenFoodFacts
│   │   ├── database/       # Helper SQLite et DAO pour les favoris et l'historique
│   │   ├── model/          # Modèles de données (Product, Nutriments, Allergen)
│   │   └── preferences/    # Gestion des préférences d'allergènes
│   ├── ui/
│   │   ├── adapter/        # Adaptateurs RecyclerView (produits et allergènes)
│   │   ├── detail/         # Activité détaillée du produit
│   │   └── main/           # MainActivity et fragments (Accueil, Favoris, Profil)
│   └── utils/              # Vérification des allergènes et helpers d'affichage
└── res/
    ├── layout/             # Fichiers XML des écrans et composants
    ├── drawable/           # Ressources graphiques et icônes
    └── values/             # Thèmes, couleurs et chaînes de caractères
```

## Installation et exécution

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/yassinebouazizi-dev/NutriGuard.git
   ```
2. Ouvrir le projet dans **Android Studio**.
3. Laisser Gradle synchroniser les dépendances.
4. Lancer l'application sur un émulateur ou sur un smartphone Android (Android 7.0 / API 24 minimum).

Pour compiler et générer le fichier APK :
- Dans Android Studio : `Build` > `Build Bundle(s) / APK(s)` > `Build APK(s)`.
- Ou via le terminal : `gradlew assembleDebug`.

## Auteur

- **Yassine Bouazizi** (bouazizi.yassine55@gmail.com)
