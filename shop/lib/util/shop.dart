import 'package:flutter/material.dart';

List shop_articles = [];

totalPrice() {
  var total = 0.0;
  for (var shopArticle in shop_articles) {
    total += shopArticle['price'] * shopArticle['quantity'];
  }
  return total.toString();
}