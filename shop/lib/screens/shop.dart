
import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/widgets/navBar.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/widgets/shop_card.dart';


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
              Expanded(
                child: ListView.builder(
                  itemCount: shop_articles.length,
                  itemBuilder: (context, index) {
                    return ShopCard(article: shop_articles[index]);
                  },
                ),
              ),
              const SizedBox(height: 10),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: <Widget>[
                  Text(
                    // ignore: prefer_interpolation_to_compose_strings
                    '${'TOTAL : ' + totalPrice()}€',
                    style: const TextStyle(
                      color: Colors.black,
                      fontSize: 25,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  TextButton(
                    onPressed: () {
                      Navigator.pushNamed(context, '/checkout');
                    },
                    style: ButtonStyle(
                      backgroundColor: MaterialStateProperty.all<Color>(
                        const Color.fromARGB(255, 255, 142, 13),
                      ),
                    ),
                    child: const Text(
                      'PAYER',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 20,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              NavBar(parentContext: context,),
            ],
          ),
        ),
      ),
    );
  }
}