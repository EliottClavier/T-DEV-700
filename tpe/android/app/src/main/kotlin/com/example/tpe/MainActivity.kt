package com.example.tpe

import android.content.Context
import androidx.annotation.NonNull
import android.provider.Settings.Secure
import android.net.wifi.WifiManager
import android.Manifest
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.net.NetworkInterface
import java.util.Collections.list

class MainActivity: FlutterActivity() {
    private val CHANNEL = "flutter.native/helper"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler{
            call, result -> 
                if (call.method.equals("getAndroidId")) {
                    result.success(getAndroidId())
                } else {
                    result.notImplemented()
                }
        }
    }

    private fun getAndroidId(): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }
}
