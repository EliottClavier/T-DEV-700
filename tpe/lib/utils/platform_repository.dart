import 'package:flutter/services.dart';

class PlatformRepository {
  static const platform = MethodChannel("flutter.native/helper");

  Future<String> getMacAddr() async {
    String mac = 'Unknown';
    try {
      final String result = await platform.invokeMethod("getAndroidId");
      print('RESULT MAC -> $result');
      mac = result;
    } on PlatformException catch (e) {
      print(e);
    }
    return Future.value(mac);
  }

  Future<String> getImei() async {
    String imei = 'Unknown';
    try {
      final String result = await platform.invokeMethod("getImei");
      print('RESULT IMEI -> $result');
      imei = result;
    } on PlatformException catch (e) {
      print(e);
    }
    return Future.value(imei);
  }
}
