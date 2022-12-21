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
    // Création de l'objet Shop
    var shop = Shop();
    Widget app = MediaQuery(
        data: const MediaQueryData(), child: MaterialApp(home: shop));

    // Vérification de l'initialisation de l'objet
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
    // Insertion de l'objet dans le widget tester
    await tester.pumpWidget(app);

    // Vérification de la présence des cartes de produit dans la liste
    expect(find.byType(ShopCard), findsNWidgets(shop_articles.length));

    // Insertion du total mis à jour dans le widget tester
    await tester.pumpWidget(app);

    expect(shop.totalOfArticles, '3');
    expect(shop.total, '5.92');
  });
}
