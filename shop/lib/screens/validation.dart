import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/router/router.dart';

import 'package:shop/connectors/requests.dart';


class Validation extends StatelessWidget {
  static const String pageName = '/validation';

  const Validation({super.key});

  void initState(BuildContext context) async {
    Future.delayed(Duration(seconds: 3), () {
      RouterShop.pushRoute(context,Shop.pageName);
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