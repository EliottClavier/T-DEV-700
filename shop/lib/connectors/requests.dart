import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;

import 'config/config.dart';


class RequestsClass {
  static String token = "";
  static final String _ip = Token.ip;

  static late StompClient _client;

  static void connect(double amount) async {
    print("Prix : "+amount.toString());
    print("Token : "+token);
    print("IP : "+_ip);
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
        print("Reponse 200");
        token=Token.fromJson(jsonDecode(response.body)).token;
        connectWebSocket(amount);
      } else {
        print("Error : "+response.statusCode.toString()+" - "+response.body.toString());
      }
    } catch (e) {
      print(e);
    }
    
  }

  static void connectWebSocket(double amount) async {
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
      if (_client.connected) {
        print('Connected and try activate');
        activateWebSocket(amount);
      } else {
        print("Not connected");
      }
    }
  }

  static void activateWebSocket(double amount) {
    _client.activate();
    print("Connected and try payment");
    pay(amount);
  }

  static void pay(double amount) {
    print("Pay : "+amount.toString()+"€");
    var json = {
      "shopName": "SHOP",
      "amount": amount
    };
    if (_client.connected) {
      _client.send(destination: '/websocket-manager/shop/pay', body: jsonEncode(json));
    } else {
      print("Not connected");
    }
  }

  static void onConnectCallback(StompFrame connectFrame) {
    print("Connected");
    dynamic processing = _client.subscribe(destination: '/user/private/shop/processing', callback: (frame) {
      print("Processing");
      print(frame.body);
    });

    dynamic transactionDone = _client.subscribe(destination: '/user/private/shop/transaction-done', callback: (frame) {
      print("Transaction done");
      print(frame.body);
    });

    dynamic transactionCancelled = _client.subscribe(destination: '/user/private/shop/transaction-cancelled', callback: (frame) {
      print("Transaction cancelled");
      print(frame.body);
    });
  }
}