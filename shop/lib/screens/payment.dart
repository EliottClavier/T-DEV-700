import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/widgets/snackBar.dart';

import '../util/shop.dart';
import '../widgets/separation.dart';


class Payment extends StatelessWidget {
  static const String pageName = '/payment';
  String? total = totalPrice();

  Payment({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Expanded(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    const Text(
                      'Paiement en cours ...',
                      style: TextStyle(
                        color: Colors.black,
                        fontSize: 30,
                      ),
                    ),
                    const SizedBox(height: 20),
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
                )
              ),
              Column(
                mainAxisAlignment: MainAxisAlignment.end,
                children: <Widget>[
                  const Separation(),
                  const SizedBox(height: 10),
                  Text(
                    '$total €',
                    style: const TextStyle(
                      color: Colors.black,
                      fontSize: 50,
                    ),
                  ),
                  const SizedBox(height: 20),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}