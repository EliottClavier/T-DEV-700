import 'package:flutter/material.dart';
import 'package:animate_do/animate_do.dart';
import 'package:shop/screens/home.dart';


class ListArticles extends StatelessWidget {
  static const String pageName = '/listeArticles';
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Container(
        color: Colors.white,
        child: Center(
          child: Text('Liste des articles'),
        ),
      ),
    );
  }
}