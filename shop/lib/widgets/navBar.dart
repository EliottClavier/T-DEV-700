import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/router/router.dart';

class NavBar extends StatefulWidget {
  final BuildContext parentContext;
  late int selectedPage;

  NavBar({super.key, required this.parentContext});

  void selectPage() {
    if (parentContext.toString().contains('ListArticles') == true) {
      selectedPage = 0;
    } else if (parentContext.toString().contains('Shop') == true) {
      selectedPage = 1;
    }
  }

  @override
  State<NavBar> createState() => _NavBar();
}

class _NavBar extends State<NavBar> {
  @override
  Widget build(BuildContext context) {
    widget.selectPage();
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
            RouterShop.pushRoute(widget.parentContext, ListArticles.pageName);
            break;
          case 1:
            RouterShop.pushRoute(widget.parentContext, Shop.pageName);
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
      currentIndex: widget.selectedPage,
    );
  }
}