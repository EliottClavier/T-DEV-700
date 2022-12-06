
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/widgets/navBar.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/widgets/shop_card.dart';
import 'package:shop/widgets/separation.dart';
import 'package:shop/router/router.dart';

class Shop extends StatefulWidget {
  static const String pageName = '/shop';
  String? total = totalPrice();
  Shop({super.key});

  @override
  State<Shop> createState() => ShopState();
}

class ShopState extends State<Shop> {

  List articlesInShop = shop_articles;

  @override
  void initState() {
    super.initState();
  }

  void updateTotal() {
    setState(() {
      widget.total = totalPrice();
    });
  }

  void removeArticle() {
    setState(() {
      articlesInShop = shop_articles;
    });
  }

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
                  itemCount: articlesInShop.length,
                  itemBuilder: (context, index) {
                    return ShopCard(article: articlesInShop[index], onQuantityChanged: updateTotal, onRemove: removeArticle);
                  },
                ),
              ),
              const Separation(),
              const SizedBox(height: 10),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(
                    'TOTAL : ${widget.total} €',
                    style: const TextStyle(
                      color: Colors.black,
                      fontSize: 25,
                      decoration: TextDecoration.none,
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
              NavBar(parentContext: context, total: widget.total,),
            ],
          ),
        ),
      ),
    );
  }
}