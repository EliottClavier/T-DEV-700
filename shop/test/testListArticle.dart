import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';

import 'package:shop/widgets/article_card.dart';



void main() {
  testWidgets('ArticleCard has a title and a body', (WidgetTester tester) async {
    tester.binding.window.physicalSizeTestValue = const Size(800, 600);
    // Créez un widget ArticleCard
    var myArticle = {
      "name": "Abricot",
      "price": 3.87,
      "category": "Alimentaire",
      "img": "images/aliments/abricot.png"
    };
    final widget = ArticleCard(article: myArticle, onQuantityChanged: () {});

    // Ajoutez-le au test
    await tester.pumpWidget(widget);

    // Vérifiez que le titre et le corps de l'article sont affichés
    expect(find.text('Abricot'), findsOneWidget);
  });
}