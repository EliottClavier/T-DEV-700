import 'package:flutter/material.dart';
import 'package:shop/screens/home.dart';
import 'package:shop/screens/listArticles.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: Home.pageName, routes: {
        Home.pageName: (context) => Home(),
        ListArticles.pageName: (context) => ListArticles(),
      },
    );
  }
}