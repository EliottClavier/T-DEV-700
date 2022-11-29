
import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/widgets/navBar.dart';


class Shop extends StatelessWidget {
  static const String pageName = '/shop';

  const Shop({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Panier',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            children: <Widget>[
              FlipInX(
                child: const Image(
                  image: AssetImage('images/logo_cash_manager.png'),
                  width: 200,
                  height: 200,
                ),
              ),
              NavBar(parentContext: context,),
            ],
          ),
        ),
      ),
    );
  }
}