# Add project specific ProGuard rules here.
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep class moe.pyropix.data.db.** { *; }
-dontwarn org.apache.poi.**
-dontwarn org.matheclipse.**
-keep class org.matheclipse.** { *; }
