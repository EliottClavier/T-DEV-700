import 'dart:convert';

import 'package:flutter/material.dart';
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
            url: "http://$_ip/websocket-manager/shop/socket",
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
  }

  static void pay() {
    print("Pay : "+totalAmount.toString()+"€");
    var json = {
      "shopName": "SHOP",
      "amount": totalAmount
    };
    _client.send(destination: '/websocket-manager/shop/pay', body: jsonEncode(json));
  }

  static void onConnectCallback(StompFrame connectFrame) {
    print("Connected");
    dynamic processing = _client.subscribe(destination: '/user/private/shop/processing', callback: (frame) {
      print("Processing");
      print(frame.body);
      Navigator.pushNamed(parentContext, Shop.pageName);
      showSnackBar(parentContext, "Aucun TPE disponible pour le moment", "error", 3);
    });

    dynamic transactionDone = _client.subscribe(destination: '/user/private/shop/transaction-done', callback: (frame) {
      print("Transaction done");
      print(frame.body);
      Navigator.pushNamed(parentContext, Validation.pageName);
    });

    dynamic transactionCancelled = _client.subscribe(destination: '/user/private/shop/transaction-cancelled', callback: (frame) {
      print("Transaction cancelled");
      print(frame.body);
      Navigator.pushNamed(parentContext, Shop.pageName);
      showSnackBar(parentContext, "La transaction a été annulée", "error", 3);
    });

    if (!paymentValidate) {
      pay();
    }
  }
}