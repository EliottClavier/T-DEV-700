import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/router/router.dart';

import 'package:shop/connectors/requests.dart';

// Class representing a validation page
class Validation extends StatelessWidget {
  // Static constant for the route name of the page
  static const String pageName = '/validation';

  // Constructor for the Validation class that takes in a required key
  const Validation({super.key});

  // Method that initializes the state of the Validation widget and return to the shop page after 3 seconds
  void initState(BuildContext context) async {
    Future.delayed(Duration(seconds: 3), () {
      RouterShop.pushRoute(context, Shop.pageName);
    });
  }

  // Method that builds and returns the widget tree for the Validation widget
  @override
  Widget build(BuildContext context) {
    initState(context);
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: const Color.fromARGB(255, 56, 193, 114),
        child: const Center(
          child: Image(
            image: AssetImage('images/validation.gif'),
          ),
        ),
      ),
    );
  }
}
