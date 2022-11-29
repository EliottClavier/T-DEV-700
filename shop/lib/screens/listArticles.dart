import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/screens/home.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/widgets/article_card.dart';
import 'package:shop/widgets/navBar.dart';


class ListArticles extends StatelessWidget {
  static const String pageName = '/listeArticles';
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Liste des articles',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            children: <Widget>[
              FlipInX(
                child: const Image(
                  image: AssetImage('images/logo_cash_manager.png'),
                  width: 200,
                  height: 200,
                ),
              ),
              Expanded(
                child: GridView.count(
                  crossAxisCount: 2,
                  children: List.generate(articles.length, (index) {
                    return ArticleCard(article: articles[index]);
                  }),
                ),
              ),
              const NavBar(),
            ],
          ),
        ),
      ),
    );
  }
}