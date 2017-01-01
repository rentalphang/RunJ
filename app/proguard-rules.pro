# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndriodEclipse\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-keepattributes EnclosingMetho
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn com.squareup.okhttp.
-dontwarn cn.bmob.newim.
-keep class com.squareup.okhttp.{*;}
-keep class com.zhy.http.okhttp.{*;}
-keep interface com.squareup.okhttp. {*;}
-dontwarn okio.*
-keep class com.google.gson. {*;}
-keep class com.google.gson.JsonObject { *; }

-keep class cn.bmob.v3.* {*;}
-keep class com.rentalphang.runj.model.bean.Comment{*;}
-keep class com.rentalphang.runj.model.bean.Dynamic{*;}
-keep class com.rentalphang.runj.model.bean.Friend{*;}
-keep class com.rentalphang.runj.model.bean.Like{*;}
-keep class com.rentalphang.runj.model.bean.RunRecord{*;}
-keep class com.rentalphang.runj.model.bean.TimeLine{*;}
-keep class com.rentalphang.runj.model.bean.User{*;}


-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-keep class de.greenrobot.event.** {*;}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}



