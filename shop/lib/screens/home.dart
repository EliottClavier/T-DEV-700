import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/router/router.dart';
import 'package:shop/screens/payment.dart';

class Home extends StatelessWidget {
  static const String pageName = '/';

  const Home({super.key});

  void initState(BuildContext context) async {
    Future.delayed(const Duration(seconds: 2), () {
      RouterShop.pushRoute(context, Payment.pageName);
      RouterShop.pushRoute(context, ListArticles.pageName);
    });
  }

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
