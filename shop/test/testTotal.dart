import 'package:flutter_test/flutter_test.dart';
import 'package:shop/util/shop.dart';

void main() {
  test("Tester la fonction qui calcule le prix total des articles dans le panier", () {
    shop_articles = [
      {'price': 1.0, 'quantity': 1},
      {'price': 2.0, 'quantity': 2},
      {'price': 3.0, 'quantity': 3},
    ];
    expect(totalPrice(), '14.0');
  });

  test("Tester la fonction qui calcule le nombre d'articles présents dans le panier", () {
    shop_articles = [
      {'price': 1.0, 'quantity': 1},
      {'price': 2.0, 'quantity': 2},
      {'price': 3.0, 'quantity': 3},
    ];
    expect(totalArticles(), '6');
  });
}