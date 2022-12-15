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
                if (call.method.equals("getMacAddr")) {
                    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    result.success(wifiManager.connectionInfo.macAddress)
                } else if (call.method.equals("getAndroidId")) {
                    result.success(getAndroidId())
                } else if(call.method.equals("getImei")) {
                    result.success(getImei())
                } else {
                    result.notImplemented()
                }
        }
    }

    private fun getMacAddr(): String {
      try {
          val all = list(NetworkInterface.getNetworkInterfaces())
          for (nif in all) {
              if (nif.name.equals("wlan0", true)) {
                  val macBytes = nif.hardwareAddress
                  if (macBytes != null) {
                      val res1 = StringBuilder()
                      for (b in macBytes) {
                          res1.append(Integer.toHexString(b.toInt() and 0xFF) + ":")
                      }
                      if (res1.isNotEmpty()) {
                          res1.deleteCharAt(res1.length - 1)
                      }
                      return res1.toString()
                  }
              }
          }
      } catch (ex: Exception) {
          // gérer l'exception
      }
      return ""
    }

    private fun getAndroidId(): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

    private fun getImei(): String {
        // Get the TelephonyManager instance
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Get the device's IMEI number
        val imei = telephonyManager.deviceId
        return imei
    }
}
