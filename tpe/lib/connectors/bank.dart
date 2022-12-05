import 'dart:ffi';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';

void loadEnv() async {
  await dotenv.load(fileName: ".env");
}

Future<dynamic> sendConnectionRequest(
    String url, Map<String, String> headers, String body) async {
  final response =
      await http.post(Uri.parse(url), headers: headers, body: body);
  if (response.statusCode == 200) {
    return response.body;
  } else {
    throw Exception('Failed to Connect with Bank');
  }
}

Future<dynamic> sendQrCodeData(String data) async {
  print("Qr code data sent : ${data}");
  return Future.value(true);
}

void sendNfcData(String data) {
  print("Nfc data sent : ${data}");
}
