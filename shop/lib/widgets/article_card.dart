import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/util/shop.dart';

class ArticleCard extends StatelessWidget {
  final Map article;

  ArticleCard({required this.article});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Card(
        child: InkWell(
          onTap: (() {
            var quantity = 1;
            for(var shopArticle in shop_articles){
              if(shopArticle['name'] == article['name']){
                shopArticle['quantity'] = shopArticle['quantity'] + 1;
                return;
              }
            }
            var shopArticle = {
              'name': article['name'],
              'price': article['price'],
              'img': article['img'],
              'quantity': quantity,
            };
            shop_articles.add(shopArticle);
          }),
          child: Container(
            width: 150,
            height: 150,
            decoration: const BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.all(
                Radius.circular(10),
              ),
              boxShadow: [
                BoxShadow(
                  color: Color(0xffDDDDDD),
                  blurRadius: 6.0,
                  spreadRadius: 2.0,
                  offset: Offset(0.0, 0.0),
                )
              ],
            ),
            child: Column(
              children: <Widget>[
                const Padding(padding: EdgeInsets.all(10)),
                Image.asset(
                  article["img"],
                  height: MediaQuery.of(context).size.height / 10,
                  width: MediaQuery.of(context).size.height / 10,
                  fit: BoxFit.cover,
                ),
                Center(
                  child: Text(
                    article["name"],
                    style: const TextStyle(
                      color: Colors.black,
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
                Center(
                  child: Text(
                    "${article["price"]}€",
                    style: const TextStyle(
                      color: Colors.black,
                      fontSize: 15,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              ],
          ),
          ),
        )
      ),
    );
  }
}