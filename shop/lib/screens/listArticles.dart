import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/widgets/article_card.dart';
import 'package:shop/widgets/navBar.dart';
import 'package:shop/widgets/category_card.dart';

class ListArticles extends StatefulWidget {
  
  static const String pageName = '/listeArticles';
  String? total = totalArticles();
  List listArticles = [];

  @override
  State<ListArticles> createState() => ListArticlesState();

  
}

class ListArticlesState extends State<ListArticles> {

  @override
  void initState() {
    super.initState();
    updateListArticles();
  }

  void updateTotal() {
    setState(() {
      widget.total = totalArticles();
    });
  }

  String selectedCategory() {
    for (var category in categories) {
      if (category.selected == true) {
        return category.name;
      }
    }
    return '';
  }

  updateListArticles() {
    String categoryName = selectedCategory();
    setState(() {
      widget.listArticles = articles.where((article) => article['category'] == categoryName).toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Liste des articles',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: Colors.white,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            SizedBox(
              height: 150,
              child: FlipInX(
                child: const Image(
                  image: AssetImage('images/logo_cash_manager.png'),
                  width: 200,
                  height: 200,
                ),
              ),
            ),
            const Padding(padding: EdgeInsets.all(10)),
            SizedBox(
              height: 50,
              child: ListView(
                scrollDirection: Axis.horizontal,
                shrinkWrap: false,
                children: categories.map((test) {
                  return CategoryCard(category: test, onCategoryChanged: updateListArticles);
                }).toList(),
              ),
            ),
            Flexible(
              child: GridView.count(
                crossAxisCount: 2,
                children: List.generate(widget.listArticles.length, (index) {
                  return ArticleCard(article: widget.listArticles[index], onQuantityChanged: updateTotal);
                }),
              ),
            ),
            NavBar(parentContext: context, total: widget.total),
          ],
        ),
      ),
    );
  }
}