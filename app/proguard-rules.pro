-verbose
-allowaccessmodification
-assumevalues class android.os.Build$VERSION {
    int SDK_INT return 21..2147483647;
}

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
#-keepclassmembernames class kotlinx.** {
#    volatile <fields>;
#}

-dontwarn com.google.errorprone.annotations.*
-dontwarn java.lang.ClassValue
-keep class java.lang.ClassValue { *; }

-keep class com.google.android.gms.measurement.AppMeasurement { *; }
-keep class com.google.android.gms.measurement.AppMeasurement$OnEventListener { *; }

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# Needed by google-http-client-android when linking against an older platform version

-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version

-dontwarn com.google.api.client.googleapis.extensions.android.**

# Needed by google-play-services when linking against an older platform version

-dontwarn com.google.android.gms.**