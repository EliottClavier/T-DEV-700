import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;
import 'package:tpe/config/security/token.dart';
import 'package:tpe/config/transaction/transaction_status.dart';
import 'package:tpe/config/environment/index.dart';
import 'package:tpe/config/security/android_id.dart';
import 'package:tpe/utils/amount.dart';
import 'package:tpe/utils/enums/transaction_type.dart';
import 'package:tpe/config/router/navigator.dart';
import 'package:tpe/utils/auth_modal.dart';

class TransactionService with ChangeNotifier {
  static final TransactionService _transactionService =
      TransactionService._internal();
  String _token = "";
  final String _baseUrl = API_URL;
  String _androidId = "";
  double _amount = 0.0;
  late BuildContext _context;
  late StompClient _client;

  TransactionType _transactionType = TransactionType.NFC;
  String _paymentId = "";

  String _sessionId = "";
  String password = "";
  String _status = "Disconnected.";

  void setStatus(status) {
    _status = status;
  }

  TransactionService getInstance() {
    return _transactionService;
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

  void setPassword(password) {
    this.password = password;
  }

  String getSessionId() {
    return _sessionId;
  }

  String getTransactionType() {
    return _transactionType.toString();
  }

  String getPaymentId() {
    return _paymentId;
  }

  void setClient(client) {
    _client = client;
  }

  StompClient getClient() {
    return _client;
  }

  Future<void> killTransaction() async {
    await killWebSocket();
    resetAttributes();
    return Future.value();
  }

  void resetAttributes() {
    _status = "Disconnected.";
    _amount = 0.0;
    _transactionType = TransactionType.NFC;
    _paymentId = "";
    _sessionId = "";
  }

  void reset() {
    handleTransactionStatus(_context, {
      "type": "RESET",
      "message": "Transaction cancelled",
    });
  }

  Future<void> payWithNfc(paymentId) async {
    navigate("/payment/sending");
    _transactionType = TransactionType.NFC;
    _paymentId = paymentId;
    await completeTransaction();
  }

  Future<void> payWithQrCode(paymentId) async {
    navigate("/payment/sending");
    _transactionType = TransactionType.QR_CODE;
    _paymentId = paymentId;
    await completeTransaction();
  }

  Future<void> init(context) async {
    _androidId = await getAndroidId();
    _context = context;
    showAuthModal();
  }

  Future<void> restart(context) async {
    _androidId = await getAndroidId();
    _context = context;
    await connect();
    return Future.value();
  }

  Future<void> connect() async {
    try {
      final response = await http.post(
          Uri.parse("$API_HTTP_PROTOCOL://$_baseUrl/auth/tpe/login"),
          headers: {"Content-Type": "application/json"},
          body: jsonEncode({"androidId": _androidId, "password": password}));
      if (response.statusCode == 200) {
        _token = Token.fromJson(jsonDecode(response.body)).token;
        setStatus("Connected to server.");
        await connectWebSocket();
      } else if (response.statusCode == 403) {
        setStatus("Waiting for device whitelist.");
      } else if (response.statusCode == 401) {
        setStatus("Bad credentials.");
        if (password != "") {
          showAuthModal();
        } else {
          _fullRegister();
        }
      } else {
        _fullRegister();
        setStatus("Connexion to server failed.");
      }
      notifyListeners();
    } catch (e) {
      setStatus(
          "Failed to contact server. Please check your internet connection.");
      await Future.delayed(const Duration(seconds: 5));
    }
    return Future.value();
  }

  Future<void> _fullRegister() async {
    final response = await http.post(
        Uri.parse("$API_HTTP_PROTOCOL://$_baseUrl/auth/tpe/register"),
        headers: {
          "Content-Type": "application/json",
          // ignore: unnecessary_string_interpolations
          "$REGISTER_HEADER": "$REGISTER_KEY"
        },
        body: jsonEncode({"androidId": _androidId}));
    if (response.statusCode == 200 || response.statusCode == 409) {
      password = jsonDecode(response.body)['password'];
      setStatus('Register successfull. Please wait for Device whitelist.');
    } else {
      onFullRegisterError();
      setStatus("Register to server failed.");
    }
    notifyListeners();
    return Future.value();
  }

  Future<void> onFullRegisterError() async {
    await Future.delayed(const Duration(seconds: 2));
    showAuthModal();
    return Future.value();
  }

  StompConfig getClientConfig() {
    return StompConfig.SockJS(
      url:
          "$API_HTTP_PROTOCOL://$_baseUrl/websocket-manager/secured/tpe/socket",
      onConnect: _onConnectCallback,
      onWebSocketError: (dynamic error) => print(error.toString()),
      stompConnectHeaders: {'Authorization': "Bearer $_token"},
      webSocketConnectHeaders: {
        'Authorization': "Bearer $_token",
        'Connection': 'upgrade',
        'Upgrade': 'WebSocket'
      },
    );
  }

  StompClient createClient() {
    return StompClient(config: getClientConfig());
  }

  Future<void> connectWebSocket() async {
    _client = createClient();
    if (_token.isNotEmpty) {
      await Future.delayed(const Duration(seconds: 1), () async {
        _activateWebSocket();
        setStatus("Connected to websocket.");
        await _synchronizeTpe();
        setStatus("TPE available for transaction.");
      });
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
    RegExp regex = RegExp(r'\/(\w+)\/websocket');
    var match = regex.firstMatch(url);
    if (match != null) {
      _sessionId = match.group(1)!;
    } else {
      _client.deactivate();
    }
    notifyListeners();
    return Future.value();
  }

  Future<void> killWebSocket() async {
    if (!_client.isActive) return;
    _client.deactivate();
    notifyListeners();
  }

  Future<void> sendKillTransaction() async {
    _client.send(destination: '/websocket-manager/tpe/cancel-transaction');
    return Future.value();
  }

  Future<void> _synchronizeTpe() async {
    await Future.delayed(const Duration(seconds: 1), () async {
      _client.send(destination: '/websocket-manager/tpe/synchronize');
    });
    return Future.value();
  }

  Future<void> completeTransaction() async {
    String transactionType =
        _transactionType == TransactionType.NFC ? "CARD" : "CHECK";
    _client.send(
        destination: '/websocket-manager/tpe/complete-transaction',
        body: "{'paymentId': '$_paymentId', 'type': '$transactionType'}");
    return Future.value();
  }

  Future<void> _onConnectCallback(StompFrame frame) {
    // ignore: unused_local_variable
    dynamic synchronizationStatus = _client.subscribe(
        destination: '/user/queue/tpe/synchronization-status/$_sessionId',
        callback: (frame) {
          setStatus(handleTransactionStatus(_context, jsonDecode(frame.body!)));
        });

    // ignore: unused_local_variable
    dynamic transactionStatus = _client.subscribe(
        destination: '/user/queue/tpe/transaction-status/$_sessionId',
        callback: (frame) {
          setStatus(handleTransactionStatus(_context, jsonDecode(frame.body!)));
        });
    notifyListeners();
    return Future.value();
  }

  void callListeners() {
    notifyListeners();
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
