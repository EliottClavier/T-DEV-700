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
import 'package:tpe/config/environment/index.dart';
import 'package:tpe/utils/android_id.dart';
import 'package:tpe/utils/amount.dart';

class TransactionService with ChangeNotifier {
  static final TransactionService _transactionService = TransactionService._internal();
  static const MethodChannel _channel = MethodChannel('bank_service');

  dynamic dotenv;

  late String _token;
  final String _baseUrl = API_URL;
  late StompClient _client;
  late String _androidId;
  late double _amount;

  late BuildContext _context;

  bool isConnectedToApi = false;
  bool isConnectedToWebSocket = false;
  bool isActiveWebSocket = false;
  bool isRegistered = false;
  bool isSynchronized = false;

  String _sessionId = "";
  String password = "-:|p4a(Lwsx0";
  String _status = "Disconnected";

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

  void setAmount(amount) {
    _amount = amount;
  }

  String getAmount() {
    return getAmountString(_amount);
  }

  Future<void> init(context) async {
    _androidId = await getAndroidId();
    _context = context;
    await _connect();
  }

  Future<void> _connect() async {
    print("connecting to server");
    final response = await http.post(
        Uri.parse("http://$_baseUrl/auth/tpe/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"androidId": _androidId, "password": password}));
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
    final response =
        await http.post(Uri.parse("http://$_baseUrl/auth/tpe/register"),
            headers: {
              "Content-Type": "application/json",
              // ignore: unnecessary_string_interpolations
              "$REGISTER_HEADER": "$REGISTER_KEY"
            },
            body: jsonEncode({"androidId": _androidId}));
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
      url: "http://$_baseUrl/websocket-manager/secured/tpe/socket",
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
    url = url.replaceAll(
        "ws://$_baseUrl/websocket-manager/secured/tpe/socket", "");
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
          handleTransactionStatus(_context, jsonDecode(frame.body!));
        });

    dynamic transactionStatus = _client.subscribe(
        destination: '/user/queue/tpe/transaction-status/$_sessionId',
        callback: (frame) {
          print("Transaction status");
          print(frame.body);
          handleTransactionStatus(_context, jsonDecode(frame.body!));
        });
    notifyListeners();
    return Future.value();
  }

  void onOpenTransaction(double amount) {
    setAmount(amount);
    setStatus("Transaction opened");
    notifyListeners();
  }

  factory TransactionService() {
    return _transactionService;
  }

  TransactionService._internal();
}
