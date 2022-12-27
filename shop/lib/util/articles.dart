import 'package:flutter/material.dart';

List articles = [
  {
    "name": "Abricot",
    "price": 3.87,
    "category": "Alimentaire",
    "img": "images/aliments/abricot.png"
  },
  {
    "name": "Banane",
    "price": 1.12,
    "category": "Alimentaire",
    "img": "images/aliments/banane.png"
  },
  {
    "name": "Carotte",
    "price": 0.93,
    "category": "Alimentaire",
    "img": "images/aliments/carotte.png"
  },
  {
    "name": "Cerise",
    "price": 1.09,
    "category": "Alimentaire",
    "img": "images/aliments/cerise.png"
  },
  {
    "name": "Épinard",
    "price": 1.89,
    "category": "Alimentaire",
    "img": "images/aliments/epinard.png"
  },
  {
    "name": "Oignon",
    "price": 0.78,
    "category": "Alimentaire",
    "img": "images/aliments/oignon.png"
  },
  {
    "name": "Olive",
    "price": 0.99,
    "category": "Alimentaire",
    "img": "images/aliments/olive.png"
  },
  {
    "name": "Patate",
    "price": 0.99,
    "category": "Alimentaire",
    "img": "images/aliments/patate.png"
  },
  {
    "name": "Poivron",
    "price": 3.15,
    "category": "Alimentaire",
    "img": "images/aliments/poivron.png"
  },
  {
    "name": "Radis",
    "price": 1.73,
    "category": "Alimentaire",
    "img": "images/aliments/radis.png"
  },
  {
    "name": "Salade",
    "price": 1.09,
    "category": "Alimentaire",
    "img": "images/aliments/salade.png"
  },
  {
    "name": "Tomate",
    "price": 1.93,
    "category": "Alimentaire",
    "img": "images/aliments/tomate.png"
  },
  {
    "name": "Altère",
    "price": 9.99,
    "category": "Sport",
    "img": "images/sports/altere.png"
  },
  {
    "name": "Balle de tennis",
    "price": 6.50,
    "category": "Sport",
    "img": "images/sports/balle_tennis.png"
  },
  {
    "name": "Balle de volley",
    "price": 20.00,
    "category": "Sport",
    "img": "images/sports/volley.png"
  },
  {
    "name": "Ballon de foot",
    "price": 14.95,
    "category": "Sport",
    "img": "images/sports/foot.png"
  },
  {
    "name": "Clubs de golf",
    "price": 35.00,
    "category": "Sport",
    "img": "images/sports/club_golf.png"
  },
  {
    "name": "Short de sport",
    "price": 11.89,
    "category": "Sport",
    "img": "images/sports/short.png"
  },
  {
    "name": "Raquettes de tennis",
    "price": 49.99,
    "category": "Sport",
    "img": "images/sports/tennis.png"
  },
  {
    "name": "Aspirateur",
    "price": 104.99,
    "category": "Menage",
    "img": "images/menage/aspirateur.png"
  },
  {
    "name": "Éponge",
    "price": 8.29,
    "category": "Menage",
    "img": "images/menage/eponge.png"
  },
  {
    "name": "Produit vaisselle",
    "price": 2.26,
    "category": "Menage",
    "img": "images/menage/vaisselle.png"
  },
  {
    "name": "Torchon",
    "price": 1.12,
    "category": "Menage",
    "img": "images/menage/torchon.png"
  },
];

class Category {
  String name;
  String img;
  bool selected;

  Category({required this.name, required this.img, required this.selected});
}

List<Category> categories = getCategories();

List<Category> getCategories() {
  List<Category> categories = [];
  for (var i = 0; i < articles.length; i++) {
    if (categories.indexWhere((category) => category.name == articles[i]["category"]) == -1) {
      bool selected = false;
      if(categories.isEmpty) {
        selected = true;
      }
      categories.add(Category(
        name: articles[i]["category"], 
        img: "images/category/"+articles[i]["category"]+".png" ?? "images/category/Default.png", 
        selected: selected
      ));
    }
  }
  return categories;
}