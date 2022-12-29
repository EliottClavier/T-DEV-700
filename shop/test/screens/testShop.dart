import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shop/main.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/widgets/shop_card.dart';

void main() {
  testWidgets('Test Shop', (WidgetTester tester) async {
    // Creating the Shop object
    var shop = Shop();
    Widget app = MediaQuery(
        data: const MediaQueryData(), child: MaterialApp(home: shop));

    // Checking object initialization
    expect(shop.totalOfArticles, '0');
    expect(shop.total, '0.0');
    expect(shop_articles, isEmpty);

    var ArticleAdd = {
      'name': articles[0]['name'],
      'price': articles[0]['price'],
      'img': articles[0]['img'],
      'quantity': 1,
    };
    shop_articles.add(ArticleAdd);
    ArticleAdd = {
      'name': articles[1]['name'],
      'price': articles[1]['price'],
      'img': articles[1]['img'],
      'quantity': 1,
    };
    shop_articles.add(ArticleAdd);
    ArticleAdd = {
      'name': articles[2]['name'],
      'price': articles[2]['price'],
      'img': articles[2]['img'],
      'quantity': 1,
    };
    shop_articles.add(ArticleAdd);

    shop = Shop();

    // Inserting the object into the widget tester
    await tester.pumpWidget(app);

    // Checking for the presence of product cards in the list
    expect(find.byType(ShopCard), findsNWidgets(shop_articles.length));

    expect(shop.totalOfArticles, '3');
    expect(shop.total, '5.92');
  });
}
