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
  late String _sessionId;
  late String _ip;
  String _status = "Disconnected";
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

  void setStatus(status) {
    _status = status;
  }

  String getStatus() {
    return _status;
  }

  Future<void> init() async {
    /* dotenv = await dotenv.load(fileName: ".env");
    _ip = dotenv.env['IP']; */
    _ip = "192.168.1.11:8080/api";
    await _connect();
    print("IP : ${_ip}");
  }

  Future<void> _connect() async {
    String macAddress = await platformRepository.getMacAddr();
    final response = await http.post(Uri.parse("http://$_ip/auth/tpe/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"mac": macAddress, "serial": "SERIAL"}));
    if (response.statusCode == 200) {
      _token = Token.fromJson(jsonDecode(response.body)).token;
      await _connectWebSocket();
      setStatus("Connected to server");
      isConnectedToApi = true;
      isRegistered = true;
    } else {
      _fullRegister();
      setStatus("Connexion to server failed.");
      throw Exception('Failed to load post');
    }
    notifyListeners();
  }

  Future<void> _fullRegister() async {
    String macAddress = await platformRepository.getMacAddr();
    final response = await http.post(Uri.parse("http://$_ip/auth/tpe/register"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"mac": macAddress, "serial": "SERIAL"}));

    if (response.statusCode == 200) {
      setStatus('Register successfull. Please wait for Device whitelist');
      isRegistered = true;
    } else {
      onFullRegisterError();
      setStatus("Register to server failed. New attempt in 5 seconds");
      throw Exception('Failed to full register tpe');
    }
    notifyListeners();
  }

  Future<void> onFullRegisterError() async {
    setStatus("Connexion attempt failed. Retry in 5 seconds");
    await Future.delayed(const Duration(seconds: 5));
    await _connect();
    return Future.value();
  }

  Future<void> _connectWebSocket() async {
    final config = StompConfig.SockJS(
      url: "http://$_ip/websocket-manager/secured/tpe/socket",
      onConnect: _onConnectCallback,
      onWebSocketError: (dynamic error) => print(error.toString()),
      stompConnectHeaders: {'Authorization': "Bearer $_token"},
      webSocketConnectHeaders: {
        'Authorization': "Bearer $_token",
        'Connection': 'upgrade',
        'Upgrade': 'WebSocket'
      },
    );
    if (_token.isNotEmpty) {
      _client = StompClient(config: config);
      await _activateWebSocket();
      isConnectedToWebSocket = true;
      setStatus("Connected to websocket");
    } else {
      setStatus("Connexion to websocket failed.");
      onFullRegisterError();
      throw Exception('Failed to load post');
    }
    notifyListeners();
  }

  Future<void> _activateWebSocket() async {
    _client.activate();
    String url = _client.config.url;
    url = url.replaceAll("ws://$_ip/websocket-manager/secured/tpe/socket", "");
    url = url.replaceAll("/websocket", "");
    _sessionId = url.replaceAll("r/^[0-9]+\//", "").split('/')[2];
    isActiveWebSocket = true;
    await _synchronizeTpe();
    notifyListeners();
  }

  Future<void> killWebSocket() async {
    _client.deactivate();
    isActiveWebSocket = false;
    notifyListeners();
  }

  Future<void> _synchronizeTpe() async {
    Future.delayed(const Duration(seconds: 1), () {
      _client.send(destination: '/websocket-manager/tpe/synchronize');
    });
  }

  Future<void> _onConnectCallback(StompFrame frame) {
    dynamic synchronizationStatus = _client.subscribe(
        destination: '/user/queue/tpe/synchronization-status/$_sessionId',
        callback: (frame) {
          print("Synchronise status");
          print(frame.body);
        });

    dynamic transactionStatus = _client.subscribe(
        destination: '/user/queue/tpe/transaction-status/$_sessionId',
        callback: (frame) {
          print("Transaction status");
          print(frame.body);
        });
    notifyListeners();
    return Future.value();
  }

  factory BankService() {
    return _bankService;
  }

  BankService._internal();
}
