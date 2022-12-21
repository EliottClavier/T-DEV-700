import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/util/articles.dart';

import 'package:shop/widgets/article_card.dart';
import 'package:shop/widgets/category_card.dart';

void main() {
  testWidgets('Test liste des articles', (WidgetTester tester) async {
    // Création de l'objet ListArticles
    final listArticlesState = ListArticles();

    // Vérification de l'initialisation de l'objet
    expect(listArticlesState.total, '0');
    expect(listArticlesState.listArticles, isEmpty);

    // Insertion de l'objet dans le widget tester
    await tester.pumpWidget(listArticlesState);

    // Vérification de la présence des catégories dans la liste
    expect(find.byType(CategoryCard), findsNWidgets(categories.length));

    // Sélection d'une catégorie
    final category = categories[0];
    category.selected = true;

    // Insertion de la liste mise à jour dans le widget tester
    await tester.pumpWidget(listArticlesState);

    // Vérification de la présence de deux articles par ligne
    expect(find.byType(ArticleCard), findsNWidgets(2));
  });
}
