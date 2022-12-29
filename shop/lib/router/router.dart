import 'package:flutter/material.dart';
import 'package:shop/screens/home.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/screens/payment.dart';
import 'package:shop/screens/validation.dart';

/* This class provides an interface to navigate between different pages in the app.
It defines the app's routes and the pages they correspond to. */
class RouterShop extends StatelessWidget {
  // Method that pushes a route to the Navigator
  static pushRoute(BuildContext context, String route) async {
    Navigator.pushNamed(context, route);
  }

  // Method that builds and returns the widget tree for the RouterShop widget
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: Home.pageName,
      routes: {
        Home.pageName: (context) => Home(),
        ListArticles.pageName: (context) => ListArticles(),
        Shop.pageName: (context) => Shop(),
        Payment.pageName: (context) => Payment(),
        Validation.pageName: (context) => const Validation(),
      },
    );
  }
}
