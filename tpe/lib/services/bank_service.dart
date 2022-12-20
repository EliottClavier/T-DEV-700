import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;
import 'package:tpe/config/token.dart';
import 'package:tpe/utils/transaction_status.dart';

class BankService with ChangeNotifier {
  static final BankService _bankService = BankService._internal();
  static const MethodChannel _channel = MethodChannel('bank_service');

  dynamic dotenv;

  late String _token;
  String _sessionId = "";
  late String _ip;
  String _status = "Disconnected";
  late StompClient _client;
  late BuildContext _context;

  bool isConnectedToApi = false;
  bool isConnectedToWebSocket = false;
  bool isActiveWebSocket = false;
  bool isRegistered = false;
  bool isSynchronized = false;

  String password = "p0;jRE:Ae6.:";
  String androidId = "123456789";

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

  Future<void> init(context) async {
    /* dotenv = await dotenv.load(fileName: ".env");
    _ip = dotenv.env['IP']; */
    _context = context;
    _ip = "10.29.125.164:8080/api";
    await _connect();
    print("IP : ${_ip}");
  }

  Future<void> _connect() async {
    print("connecting to server");
    final response = await http.post(Uri.parse("http://$_ip/auth/tpe/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"androidId": androidId, "password": password}));
    print(response.body);
    print(response.statusCode);
    if (response.statusCode == 200) {
      _token = Token.fromJson(jsonDecode(response.body)).token;
      await _connectWebSocket();
      setStatus("Connected to server");
      isConnectedToApi = true;
      isRegistered = true;
    } else {
      _fullRegister();
      setStatus("Connexion to server failed.");
    }
    notifyListeners();
  }

  Future<void> _fullRegister() async {
    print("full register to server");
    final response = await http.post(Uri.parse("http://$_ip/auth/tpe/register"),
        headers: {
          "Content-Type": "application/json",
          "Authorization-TPE-Register": "LDQRLohg4K8eEqWZ1dWmG87dTUaOMJPr"
        },
        body: jsonEncode({"androidId": androidId}));
    print("fullRegister response : ${response.body}");
    print("fullRegister response status : ${response.statusCode}");
    if (response.statusCode == 200) {
      password = jsonDecode(response.body)['password'];
      setStatus('Register successfull. Please wait for Device whitelist');
      isRegistered = true;
    } else {
      onFullRegisterError();
      setStatus("Register to server failed. New attempt in 120 seconds");
    }
    notifyListeners();
  }

  Future<void> onFullRegisterError() async {
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
    }
    notifyListeners();
    return Future.value();
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
    print("Synchronize TPE");
    await Future.delayed(const Duration(seconds: 1), () {
      _client.send(destination: '/websocket-manager/tpe/synchronize');
    });
    print("Synchronize TPE sent");
  }

  Future<void> completeTransaction() async {
    print("Complete transaction");
    _client.send(
        destination: '/websocket-manager/tpe/complete-transaction',
        body: "{'paymentId': 'PAYMENT_ID', 'type': 'NFC'}");
  }

  Future<void> _onConnectCallback(StompFrame frame) {
    dynamic synchronizationStatus = _client.subscribe(
        destination: '/user/queue/tpe/synchronization-status/$_sessionId',
        callback: (frame) {
          print("Synchronise status");
          print(frame.body);
          handleTransactionStatus(_context, frame.body);
        });

    dynamic transactionStatus = _client.subscribe(
        destination: '/user/queue/tpe/transaction-status/$_sessionId',
        callback: (frame) {
          print("Transaction status");
          print(frame.body);
          handleTransactionStatus(_context, frame.body);
        });
    notifyListeners();
    return Future.value();
  }

  factory BankService() {
    return _bankService;
  }

  BankService._internal();
}
