import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:stomp_dart_client/parser.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;

import 'package:shop/widgets/snackBar.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/screens/validation.dart';
import 'config/config.dart';


class RequestsClass {
  static String token = "";
  static final String _ip = Token.ip;
  static double totalAmount = 0.0;
  static bool paymentValidate = false;
  static late StompClient _client;
  static late BuildContext parentContext;
  static String _sessionId = "";

  static connect(double amount, BuildContext context) async {
    try {
      final response = await http.post(Uri.parse("http://$_ip/auth/shop/login"),
        headers: {
          "Content-Type": "application/json"
        },
        body: jsonEncode({
        "name": "SHOP",
        "password": "PASSWORD"
      }));
      print(response);
      if (response.statusCode == 200) {
        token=Token.fromJson(jsonDecode(response.body)).token;
        connectWebSocket();
        totalAmount = amount;
        parentContext = context;
      } else {
        print("Error : "+response.statusCode.toString()+" - "+response.body.toString());
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
            url: "http://$_ip/websocket-manager/secured/shop/socket",
            onConnect: onConnectCallback,
            onWebSocketError: (dynamic error) => print(error.toString()),
            stompConnectHeaders: {'Authorization': "Bearer $token"},
            webSocketConnectHeaders: {'Authorization': "Bearer $token", 'Connection': 'upgrade', 'Upgrade': 'WebSocket'},
          ),
        );
        activateWebSocket();
      }
    } catch (e) {
      print(e);
    }
  }

  static void activateWebSocket() {
    _client.activate();
    String url = _client.config.url;
    url = url.replaceAll(
        "ws://$_ip/websocket-manager/secured/shop/socket",  "");
    url = url.replaceAll("/websocket", "");
    _sessionId = url.replaceAll("r/^[0-9]+\//", "").split('/')[2];
  }

  static void pay() {
    print("Pay : "+totalAmount.toString()+"€");
    var json = {
      "token": token,
      "sessionId": _sessionId,
      "amount": 30
    };
    _client.send(destination: '/websocket-manager/shop/pay', body: jsonEncode(json));
  }

  static void onConnectCallback(StompFrame connectFrame) {
    print("Connected");
    dynamic transactionStatus = _client.subscribe(destination: '/user/queue/shop/transaction-status/$_sessionId', callback: (frame) {
      var response = Response.fromJson(jsonDecode(frame.body!));
      switch (response.type) {
        case "TRANSACTION_DONE":
          paymentValidate = true;
          Navigator.pushNamed(parentContext, Validation.pageName);
          break;
        case "TRANSACTION_ERROR":
          paymentValidate = true;
          Navigator.pushNamed(parentContext, Shop.pageName);
          showSnackBar(parentContext, "Erreur lors de la transaction", "error", 3);
          break;
        case "NO_TPE_FOUND":
          paymentValidate = true;
          Navigator.pushNamed(parentContext, Shop.pageName);
          showSnackBar(parentContext, "Aucun TPE disponible pour le moment", "error", 3);
          break;
        case "NO_TPE_SELECTED":
          paymentValidate = true;
          Navigator.pushNamed(parentContext, Shop.pageName);
          showSnackBar(parentContext, "Une erreur est survenue lors de la sélection d'un TPE", "error", 3);
          break;
        case "TRANSACTION_OPENED":
          paymentValidate = true;
          Navigator.pushNamed(parentContext, Shop.pageName);
          showSnackBar(parentContext, "Le paiement est disponible sur un TPE", "success", 3);
          break;
        case "NOT_FOUND":
          paymentValidate = true;
          Navigator.pushNamed(parentContext, Shop.pageName);
          showSnackBar(parentContext, "Erreur lors de la connexion avec la banque", "error", 3);
          break;
      }
    });
    if (!paymentValidate) {
      pay();
    }
  }
}