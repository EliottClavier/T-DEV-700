import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/util/shop.dart';

class ShopCard extends StatelessWidget {
  final Map article;

  const ShopCard({super.key, required this.article});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Card(
        child: Container(
          width: MediaQuery.of(context).size.width - 10,
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
            children: <Widget>[
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
                  article["price"],
                  style: const TextStyle(
                    color: Colors.black,
                    fontSize: 15,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
              Center(
                child: Text(
                  article["quantity"].toString(),
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
    );
  }
}