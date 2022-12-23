import 'package:android_id/android_id.dart';

Future<String> getAndroidId() async {
  AndroidId androidIdPlugin = const AndroidId();
  String androidId;

  try {
    androidId = await androidIdPlugin.getId() ?? 'Unknown ID';
  } catch (e) {
    throw Exception(e);
  }

  return androidId;
}
