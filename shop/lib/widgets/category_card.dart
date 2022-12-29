import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:shop/util/articles.dart';
import 'package:shop/util/shop.dart';

// Class representing a card for a specific category
class CategoryCard extends StatelessWidget {
  // Final variable for the Category object
  final Category category;
  // Final variable for the callback function
  final Function onCategoryChanged;

  // Constructor for the CategoryCard class, which takes in a required Category object and a required callback function
  CategoryCard({required this.category, required this.onCategoryChanged});

  // Function that returns the background color for the card
  Color? getCardColor() {
    if (category.selected == true) {
      return const Color.fromARGB(255, 184, 72, 96);
    }
    return Colors.white;
  }

  // Function that returns the text color for the card
  Color getTextColor() {
    if (category.selected == true) {
      return Colors.white;
    }
    return Colors.black;
  }

  // Method that builds and returns the widget tree for the CategoryCard
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Card(
          child: InkWell(
        onTap: (() {
          for (var shopCategory in categories) {
            if (shopCategory.name == category.name) {
              shopCategory.selected = true;
            } else {
              shopCategory.selected = false;
            }
          }
          onCategoryChanged();
        }),
        child: Container(
          height: 50,
          decoration: BoxDecoration(
            color: getCardColor(),
            borderRadius: const BorderRadius.all(
              Radius.circular(10),
            ),
            boxShadow: const [
              BoxShadow(
                color: Color(0xffDDDDDD),
                blurRadius: 6.0,
                spreadRadius: 2.0,
                offset: Offset(0.0, 0.0),
              )
            ],
          ),
          child: Row(
            children: <Widget>[
              const Padding(padding: EdgeInsets.all(10)),
              Image.asset(
                category.img,
                height: MediaQuery.of(context).size.height / 20,
                width: MediaQuery.of(context).size.height / 20,
                fit: BoxFit.cover,
              ),
              const Padding(padding: EdgeInsets.all(10)),
              Center(
                child: Text(
                  category.name,
                  style: TextStyle(
                    color: getTextColor(),
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
              const Padding(padding: EdgeInsets.all(10)),
            ],
          ),
        ),
      )),
    );
  }
}
