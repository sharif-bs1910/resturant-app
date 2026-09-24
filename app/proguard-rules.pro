-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepclassmembers @kotlinx.serialization.Serializable class com.noshitechinc.restaurant.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
