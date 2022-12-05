import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/util/shop.dart';

class ShopCard extends StatefulWidget {
  final Map article;

  const ShopCard({super.key, required this.article});

  @override
  State<ShopCard> createState() => _ShopCard();
}

class _ShopCard extends State<ShopCard> {

  void quantityModifier(int quantity) {
    for (var shopArticle in shop_articles) {
      if (shopArticle['name'] == widget.article['name']) {
        shopArticle['quantity'] = quantity;
        break;
      }
    }
    setState(() {
      widget.article['quantity'] = quantity;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Card(
        child: Container(
          width: MediaQuery.of(context).size.width - 20,
          height: 100,
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
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: <Widget>[
              Image.asset(
                widget.article["img"],
                height: MediaQuery.of(context).size.height / 10,
                width: MediaQuery.of(context).size.height / 10,
                fit: BoxFit.cover,
              ),
              const SizedBox(width: 10),
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Center(
                    child: Text(
                      widget.article["name"].toString(),
                      style: const TextStyle(
                        color: Colors.black,
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                  Row(
                    children: <Widget>[
                      IconButton(
                        icon: const Icon(Icons.remove),
                        onPressed: () {
                          var quantity = widget.article["quantity"] - 1;
                          quantityModifier(quantity);
                        },
                      ),
                      Text(
                        widget.article["quantity"].toString(),
                        style: const TextStyle(
                          color: Colors.black,
                          fontSize: 15,
                        ),
                        textAlign: TextAlign.center,
                      ),
                      IconButton(
                        icon: const Icon(Icons.add),
                        onPressed: () {
                          var quantity = widget.article["quantity"] + 1;
                          quantityModifier(quantity);
                        },
                      ),
                      const SizedBox(width: 10),
                      Center(
                        child: Text(
                          "${widget.article["price"]}€",
                          style: const TextStyle(
                            color: Colors.black,
                            fontSize: 15,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              Align(
                alignment: Alignment.centerRight,
                child: IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () {
                    shop_articles.remove(widget.article);
                    setState(() {});
                  },
                ),
              ),
            ],
          ),
        ),
      )
    );
  }
}
