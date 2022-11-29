
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
              NavBar(parentContext: context,),
            ],
          ),
        ),
      ),
    );
  }
}