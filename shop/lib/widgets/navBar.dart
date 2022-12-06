import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/router/router.dart';

class NavBar extends StatefulWidget {
  final BuildContext parentContext;
  final String? total;
  late int selectedPage;
  

  NavBar({super.key, required this.parentContext, required this.total});

  void selectPage() {
    if (parentContext.toString().contains('ListArticles') == true) {
      selectedPage = 0;
    } else if (parentContext.toString().contains('Shop') == true) {
      selectedPage = 1;
    }
  }

  @override
  State<NavBar> createState() => NavBarState();
}

class NavBarState extends State<NavBar> {

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
        BottomNavigationBarItem(
          icon: Stack(
            clipBehavior: Clip.none,
            children: <Widget>[
              const Icon(Icons.shopping_cart),
              Positioned(
                left: 10,
                top: -5,
                child: Container(
                  padding: const EdgeInsets.all(2),
                  decoration: BoxDecoration(
                    color: Colors.red,
                    borderRadius: BorderRadius.circular(6),
                  ),
                  constraints: const BoxConstraints(
                    minWidth: 10,
                    minHeight: 8,
                  ),
                  child: Text(
                    '${widget.total}€',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 9,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              )
            ],
          ),
          label: 'Panier',
        ),
      ],
      currentIndex: widget.selectedPage,
    );
  }
}