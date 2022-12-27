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
    // Creating the ListArticles object
    final listArticlesState = ListArticles();

    // Checking object initialization
    expect(listArticlesState.total, '0');
    expect(listArticlesState.listArticles, isEmpty);

    // Inserting the object into the widget tester
    await tester.pumpWidget(listArticlesState);

    // Checking for the presence of categories in the list
    expect(find.byType(CategoryCard), findsNWidgets(categories.length));

    // Selecting a category
    final category = categories[0];
    category.selected = true;

    // Inserting the updated list into the widget tester
    await tester.pumpWidget(listArticlesState);

    // Checking for the presence of two articles per line
    expect(find.byType(ArticleCard), findsNWidgets(2));
  });
}
