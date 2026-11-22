# ProGuard rules for NutriGuard

# Keep Retrofit and Gson models
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.nutriguard.app.data.model.** { *; }
-keep class retrofit2.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Glide
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.** { *; }
