import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/widgets/snackBar.dart';


class Payment extends StatelessWidget {
  static const String pageName = '/payment';

  const Payment({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Image(
                image: AssetImage('images/man_wait.gif'),
                width: 200,
                height: 200,
              ),
              TextButton(
                onPressed: () {
                  Navigator.pushNamed(context, Shop.pageName);
                  showSnackBar(context, "La transaction a été annulée", "error", 3);
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