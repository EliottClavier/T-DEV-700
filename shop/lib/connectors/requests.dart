import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:shop/util/shop.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;

import 'package:shop/widgets/snackBar.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/screens/validation.dart';
import 'config/config.dart';
import 'package:shop/config/index.dart';

class RequestsClass {
  static String token = "";
  static final String _url = Token.url;
  static double totalAmount = 0.0;
  static bool paymentSended = false;
  static late StompClient _client;
  static late BuildContext parentContext;
  static String _sessionId = "";

  static connect(double amount, BuildContext context) async {
    try {
      final response = await http.post(
          Uri.parse("$HTTP_PROTOCOL://$_url/auth/shop/login"),
          headers: {"Content-Type": "application/json"},
          body: jsonEncode({"name": SHOP_USERNAME, "password": SHOP_PASSWORD}));
      if (response.statusCode == 200) {
        token = Token.fromJson(jsonDecode(response.body)).token;
        connectWebSocket();
        totalAmount = amount;
        parentContext = context;
      } else {
        if (response.statusCode == 403) {
          print("Error : ${response.statusCode} - ${response.body}");
          Navigator.pushNamed(context, Shop.pageName);
          showSnackBar(
              context,
              "L'application n'a pas les bon identifiants pour se connecter au serveur",
              "error",
              3);
        } else {
          print("Error : ${response.statusCode} - ${response.body}");
          Navigator.pushNamed(context, Shop.pageName);
          showSnackBar(
              parentContext,
              "L'application a rencontré une erreur lors de la connexion au serveur",
              "error",
              3);
        }
      }
    } catch (e) {
      print(e);
    }
  }

  static void connectWebSocket() {
    try {
      if (token.isNotEmpty) {
        _client = StompClient(
          config: StompConfig.SockJS(
            url: "$HTTP_PROTOCOL://$_url/websocket-manager/secured/shop/socket",
            onConnect: onConnectCallback,
            onWebSocketError: (dynamic error) => print(error.toString()),
            stompConnectHeaders: {'Authorization': "Bearer $token"},
            webSocketConnectHeaders: {
              'Authorization': "Bearer $token",
              'Connection': 'upgrade',
              'Upgrade': 'WebSocket'
            },
          ),
        );
        activateWebSocket();
      }
    } catch (e) {
      print(e);
    }
  }

  static void activateWebSocket() async {
    _client.activate();
    String url = _client.config.url;
    RegExp regex = RegExp(r'\/(\w+)\/websocket');
    var match = regex.firstMatch(url);
    if (match != null) {
      _sessionId = match.group(1)!;
    } else {
      _client.deactivate();
    }
  }

  static void pay() {
    var json = {"token": token, "sessionId": _sessionId, "amount": totalAmount};
    _client.send(
        destination: '/websocket-manager/shop/pay', body: jsonEncode(json));
  }

  static void cancelPayment() {
    _client.send(destination: '/websocket-manager/shop/cancel-transaction');
  }

  static void onConnectCallback(StompFrame connectFrame) {
    dynamic transactionStatus = _client.subscribe(
        destination: '/user/queue/shop/transaction-status/$_sessionId',
        callback: (frame) {
          var response = Response.fromJson(jsonDecode(frame.body!));
          switch (response.type) {
            case "TRANSACTION_DONE":
              paymentSended = true;
              _client.deactivate();
              shop_articles = [];
              Navigator.pushNamed(parentContext, Validation.pageName);
              break;
            case "TRANSACTION_ERROR":
              paymentSended = true;
              _client.deactivate();
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(
                  parentContext, "Erreur lors de la transaction", "error", 3);
              break;
            case "NO_TPE_FOUND":
              paymentSended = true;
              _client.deactivate();
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(parentContext, "Aucun TPE disponible pour le moment",
                  "error", 3);
              break;
            case "NO_TPE_SELECTED":
              paymentSended = true;
              _client.deactivate();
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(
                  parentContext,
                  "Une erreur est survenue lors de la sélection d'un TPE",
                  "error",
                  3);
              break;
            case "TRANSACTION_OPENED":
              paymentSended = true;
              showSnackBar(parentContext,
                  "Le paiement est disponible sur un TPE", "success", 3);
              break;
            case "NOT_FOUND":
              paymentSended = true;
              _client.deactivate();
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(parentContext,
                  "Erreur lors de la connexion avec la banque", "error", 3);
              break;
            case "TRANSACTION_TIMED_OUT":
              paymentSended = true;
              _client.deactivate();
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(parentContext, "Transaction timed out", "error", 3);
              break;
            case "TRANSACTION_CANCELLED":
              paymentSended = true;
              _client.deactivate();
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(
                  parentContext, "La transaction a été annulée", "error", 3);
              break;
            default:
              Navigator.pushNamed(parentContext, Shop.pageName);
              showSnackBar(
                  parentContext, "Erreur lors de la transaction", "error", 3);
              break;
          }
        });
    if (!paymentSended) {
      pay();
    }
  }
}
