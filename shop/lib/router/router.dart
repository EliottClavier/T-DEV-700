import 'package:flutter/material.dart';
import 'package:shop/screens/home.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/screens/payment.dart';
import 'package:shop/widgets/navBar.dart';

class RouterShop extends StatelessWidget {

  static pushRoute(BuildContext context, String route) async {
      Navigator.pushNamed(context, route);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: Home.pageName, routes: {
        Home.pageName: (context) => Home(),
        ListArticles.pageName: (context) => ListArticles(),
        Shop.pageName: (context) => Shop(),
        Payment.pageName: (context) => Payment(),
      },
    );
  }
}