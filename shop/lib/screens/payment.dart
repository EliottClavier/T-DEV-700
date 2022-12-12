import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/router/router.dart';


class Payment extends StatelessWidget {
  static const String pageName = '/payment';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Image(
                image: AssetImage('images/waiting.gif'),
                width: 200,
                height: 200,
              ),
              TextButton(
                onPressed: () {
                  Navigator.pushNamed(context, Shop.pageName);
                },
                style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.all<Color>(
                    const Color.fromARGB(255, 255, 142, 13),
                  ),
                ),
                child: const Text(
                  'Annuler le paiement',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}