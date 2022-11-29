import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/screens/router.dart';
class NavBar extends StatelessWidget {
  final BuildContext parentContext;
  late final int selectedPage;

  NavBar({super.key, required this.parentContext});

  void selectPage() {
    switch (parentContext.toString()) {
      case 'ListArticles':
        selectedPage = 0;
        break;
      case 'Shop':
        selectedPage = 1;
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    selectPage();
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      backgroundColor: const Color.fromARGB(255, 184, 72, 96),
      selectedItemColor: Colors.white,
      unselectedItemColor: Colors.white.withOpacity(.60),
      selectedFontSize: 14,
      unselectedFontSize: 14,
      onTap: (value) {
        // Respond to item press.
        switch (value) {
          case 0:
            RouterShop.pushRoute(parentContext, ListArticles.pageName);
            break;
          case 1:
            RouterShop.pushRoute(parentContext, Shop.pageName);
            break;
          default:
        }
      },
      // ignore: prefer_const_literals_to_create_immutables
      items: [
        const BottomNavigationBarItem(
          label: 'Home',
          icon: Icon(Icons.home),
        ),
        const BottomNavigationBarItem(
          label: 'Panier',
          icon: Icon(Icons.shopping_cart),
        ),
      ],
      currentIndex: selectedPage,
    );
  }
}