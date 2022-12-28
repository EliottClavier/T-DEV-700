import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/connectors/requests.dart';
import 'package:shop/screens/payment.dart';
import 'package:shop/widgets/navBar.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/widgets/shop_card.dart';
import 'package:shop/widgets/separation.dart';
import 'package:shop/router/router.dart';
import 'package:shop/widgets/snackBar.dart';

// Class representing a shop page
class Shop extends StatefulWidget {
  // Static constant for the route name of the page
  static const String pageName = '/shop';
  // Static late BuildContext variable for the context of the shop page
  static late BuildContext contextShop;
  // Variable for the total number of articles in the shop
  String? totalOfArticles = totalArticles();
  // Variable for the total price of the articles in the shop
  String? total = totalPrice();
  // Constructor for the Shop class that takes in a required key
  Shop({super.key});

  // Method that creates and returns the state for the Shop widget
  @override
  State<Shop> createState() => ShopState();
}

// Class representing the state for the Shop widget
class ShopState extends State<Shop> {
  // List of articles in the shop
  List articlesInShop = shop_articles;

  // Method that initializes the state of the ShopState object
  @override
  void initState() {
    super.initState();
  }

  // Method that updates the total price and number of articles in the shop
  void updateTotal() {
    setState(() {
      widget.total = totalPrice();
      widget.totalOfArticles = totalArticles();
    });
  }

  // Method that updates the list of articles in the shop
  void removeArticle() {
    setState(() {
      articlesInShop = shop_articles;
    });
  }

  // Method that builds and returns the widget tree for the Shop widget
  @override
  Widget build(BuildContext context) {
    Shop.contextShop = context;
    return Scaffold(
      body: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            children: <Widget>[
              SizedBox(
                height: 150,
                child: FlipInX(
                  child: const Image(
                    image: AssetImage('images/logo_cash_manager.png'),
                    width: 200,
                    height: 200,
                  ),
                ),
              ),
              const Padding(padding: EdgeInsets.all(5)),
              Expanded(
                child: ListView.builder(
                  itemCount: articlesInShop.length,
                  itemBuilder: (context, index) {
                    return ShopCard(
                        article: articlesInShop[index],
                        onQuantityChanged: updateTotal,
                        onRemove: removeArticle);
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
                      if (widget.total == '0.0') {
                        showSnackBar(
                            context,
                            "Vous n'avez pas d'articles dans votre panier",
                            "error",
                            3);
                      } else {
                        RequestsClass.connect(
                            double.parse(widget.total!), context);
                        Navigator.pushNamed(context, Payment.pageName);
                      }
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
              NavBar(
                parentContext: context,
                total: widget.totalOfArticles,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
