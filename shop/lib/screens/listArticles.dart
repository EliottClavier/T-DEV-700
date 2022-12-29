import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/widgets/article_card.dart';
import 'package:shop/widgets/navBar.dart';
import 'package:shop/widgets/category_card.dart';

// Class representing a list of articles
class ListArticles extends StatefulWidget {
  // Static constant for the route name of the page
  static const String pageName = '/listeArticles';
  // Variable for the total number of articles in the shop
  String? total = totalArticles();
  // List to store the articles that are displayed in the widget
  List listArticles = [];

  // Constructor for the ListArticles class that takes in a required key
  ListArticles({super.key});

  // Method to create and return the state of the ListArticles widget
  @override
  State<ListArticles> createState() => ListArticlesState();
}

// Class representing the state of the ListArticles widget
class ListArticlesState extends State<ListArticles> {
  // Method that is called when the ListArticles widget is initialized
  @override
  void initState() {
    super.initState();
    updateListArticles();
  }

  // Method to update the total number of articles in the shop
  void updateTotal() {
    setState(() {
      widget.total = totalArticles();
    });
  }

  // Method to return the name of the currently selected category
  String selectedCategory() {
    for (var category in categories) {
      if (category.selected == true) {
        return category.name;
      }
    }
    return '';
  }

  // Method to update the list of articles displayed in the widget based on the currently selected category
  updateListArticles() {
    String categoryName = selectedCategory();
    setState(() {
      widget.listArticles = articles
          .where((article) => article['category'] == categoryName)
          .toList();
    });
  }

  // Method to build and return the widget tree for the ListArticles widget
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
                  return CategoryCard(
                      category: test, onCategoryChanged: updateListArticles);
                }).toList(),
              ),
            ),
            Flexible(
              child: GridView.count(
                crossAxisCount: 2,
                children: List.generate(widget.listArticles.length, (index) {
                  return ArticleCard(
                      article: widget.listArticles[index],
                      onQuantityChanged: updateTotal);
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
