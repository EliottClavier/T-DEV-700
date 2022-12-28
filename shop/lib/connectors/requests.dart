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

/* This class is responsible for making requests to the server and handling the websocket connection with it.
It contains functions to connect to the server using a HTTP request, to connect to the websocket and to send a payment request or cancel a payment request through the websocket.
It also contains a callback function that is called whenever a message is received on the websocket, which updates the state of the application depending on the type of message received. */
class RequestsClass {
  // Token for authenticated requests to the server
  static String token = "";
  // Base URL for the server
  static final String _url = Token.url;
  // Total amount of the payment
  static double totalAmount = 0.0;
  // Whether a payment has been successfully sent
  static bool paymentSended = false;
  // Websocket client
  static late StompClient _client;
  // Build context of the parent widget
  static late BuildContext parentContext;
  // Session ID of the websocket connection
  static String _sessionId = "";

  /* Connects to the server using a HTTP request and initiates the websocket connection
  If the connection is successful, the websocket connection is initiated and the total amount of the payment is set to the amount parameter 
  and the parent context is set to the context parameter of the function call */
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

  /* Connects to a WebSocket using the specified url, authorization token, and connection headers
  If the connection is successful, the onConnect callback is called
  If there is an error with the WebSocket, the onWebSocketError callback is called with the error as an argument */
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

  // Activates the websocket connection
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

  // Sends a payment request through the websocket connection
  static void pay() {
    var json = {"token": token, "sessionId": _sessionId, "amount": totalAmount};
    _client.send(
        destination: '/websocket-manager/shop/pay', body: jsonEncode(json));
  }

  // Sends a cancel payment request through the websocket connection
  static void cancelPayment() {
    _client.send(destination: '/websocket-manager/shop/cancel-transaction');
  }

  // Callback function called whenever a message is received on the websocket connection
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
