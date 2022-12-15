import 'dart:ffi';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:tpe/config/token.dart';
import 'package:tpe/utils/platform_repository.dart';
import 'package:provider/provider.dart';

enum Actions { SendQrCodeData, SendNfcData, Connect }

class BankService with ChangeNotifier {
  static final BankService _bankService = BankService._internal();
  static const MethodChannel _channel = MethodChannel('bank_service');

  dynamic dotenv;
  PlatformRepository platformRepository = PlatformRepository();

  late String _token;
  late String _ip;
  late StompClient _client;

  bool isConnectedToApi = false;
  bool isConnectedToWebSocket = false;
  bool isActiveWebSocket = false;
  bool isRegistered = false;
  bool isSynchronized = false;

  void printStatus() {
    print("isConnectedToApi : ${isConnectedToApi}");
    print("isConnectedToWebSocket : ${isConnectedToWebSocket}");
    print("isActiveWebSocket : ${isActiveWebSocket}");
    print("isRegistered : ${isRegistered}");
    print("isSynchronized : ${isSynchronized}");
  }

  Future<void> init() async {
    /* dotenv = await dotenv.load(fileName: ".env");
    _ip = dotenv.env['IP']; */
    _ip = "192.168.1.20:8080/api";
    await _connect();
    await _connectWebSocket();
    _activateWebSocket();
    /* _synchronizeTpe(); */
    print("IP : ${_ip}");
  }

  Future<void> _connect() async {
    String macAddress = await platformRepository.getMacAddr();
    print("Mac Address : ${macAddress}");
    final response = await http.post(Uri.parse("http://$_ip/auth/tpe/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"mac": "MAC", "serial": "SERIAL"}));

    if (response.statusCode == 200) {
      _token = Token.fromJson(jsonDecode(response.body)).token;
      isConnectedToApi = true;
      isRegistered = true;
    } else {
      // Send HTTP register
      _fullRegister();
      throw Exception('Failed to load post');
    }
  }

  Future<void> _fullRegister() async {
    String macAddress = await platformRepository.getMacAddr();
    final response = await http.post(Uri.parse("http://$_ip/auth/tpe/register"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"mac": macAddress, "serial": "SERIAL"}));

    if (response.statusCode == 200) {
      print('Register successfull. Please wait TPE activation');
      isRegistered = true;
    } else {
      // Send HTTP register
      throw Exception('Failed to full register tpe');
    }
  }

  Future<void> _connectWebSocket() async {
    final config = StompConfig.SockJS(
      url: "http://$_ip/websocket-manager/tpe/socket",
      onConnect: _onConnectCallback,
      onWebSocketError: (dynamic error) => print(error.toString()),
      stompConnectHeaders: {'Authorization': "Bearer $_token"},
      webSocketConnectHeaders: {
        'Authorization': "Bearer $_token",
        'Connection': 'upgrade',
        'Upgrade': 'WebSocket'
      },
    );
    _client = StompClient(config: config);
    isConnectedToWebSocket = true;
    notifyListeners();
  }

  Future<void> _activateWebSocket() async {
    _client.activate();
    isActiveWebSocket = true;
    notifyListeners();
  }

  Future<void> _killWebSocket() async {
    _client.deactivate();
    isActiveWebSocket = false;
    notifyListeners();
  }

  Future<void> _synchronizeTpe() async {
    _client.subscribe(
        destination: "/user/queue/sync",
        callback: (StompFrame frame) {
          print("Synchronize TPE");
          print(frame.body);
          isSynchronized = true;
          notifyListeners();
        });
  }

  Future<void> _onConnectCallback(StompFrame frame) {
    print("Connected to websocket");
    print(frame.body);
    notifyListeners();
    return Future.value();
  }

  factory BankService() {
    return _bankService;
  }

  BankService._internal();
}
