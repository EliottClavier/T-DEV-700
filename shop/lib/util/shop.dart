import 'package:flutter/material.dart';

List shop_articles = [];

totalPrice() {
  var total = 0.0;
  for (var shopArticle in shop_articles) {
    total += shopArticle['price'] * shopArticle['quantity'];
  }
  total = double.parse(total.toStringAsFixed(2));
  return total.toString();
}

totalArticles() {
  num totalArticles = 0;
  for (var shopArticle in shop_articles) {
    totalArticles += shopArticle['quantity']!;
  }
  return totalArticles.toString();
}
