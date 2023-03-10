import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/screens/listArticles.dart';
import 'package:shop/screens/shop.dart';
import 'package:shop/util/shop.dart';
import 'package:shop/router/router.dart';

class NavBar extends StatefulWidget {
  // BuildContext of the parent widget
  final BuildContext parentContext;
  // Total quantity of items in the shopping cart
  final String? total;
  // Index of the currently selected page
  late int selectedPage = 0;

  // Constructor for NavBar, which takes in a required parent context and a required total
  NavBar({super.key, required this.parentContext, required this.total});

  // Function to determine the selected page based on the parent context
  void selectPage() {
    if (parentContext.toString().contains('ListArticles') == true) {
      selectedPage = 0;
    } else if (parentContext.toString().contains('Shop') == true) {
      selectedPage = 1;
    }
  }

  // Create the state for the NavBar widget
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
                    '${widget.total}',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 14,
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
