import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/router/router.dart';
import 'package:shop/screens/payment.dart';

// Home is a stateless widget representing the home screen of the app
class Home extends StatelessWidget {
  // Constants representing the route name and the default key for the widget
  static const String pageName = '/';

  // Constructor for the Payment class that takes in a required key
  const Home({super.key});

  // Initialize the state of the widget and push the route to the ListArticles page after 2 seconds
  void initState(BuildContext context) async {
    Future.delayed(const Duration(seconds: 2), () {
      RouterShop.pushRoute(context, Payment.pageName);
      RouterShop.pushRoute(context, ListArticles.pageName);
    });
  }

  // Build the widget tree for the Home widget
  @override
  Widget build(BuildContext context) {
    initState(context);
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: Colors.white,
        child: Center(
          child: FlipInX(
            child: Image(
              image: AssetImage('images/logo_cash_manager.png'),
            ),
          ),
        ),
      ),
    );
  }
}
