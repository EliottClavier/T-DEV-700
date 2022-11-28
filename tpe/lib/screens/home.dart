import 'dart:math';

import 'package:flutter/material.dart';
import 'package:tpe/screens/payment.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  static const String _title = 'Payment Terminal Home';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: const HomeScreenStatefulWidget(),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class HomeScreenStatefulWidget extends StatefulWidget {
  const HomeScreenStatefulWidget({super.key});

  @override
  State<HomeScreenStatefulWidget> createState() =>
      _HomeScreenStatefulWidgetState();
}

class _HomeScreenStatefulWidgetState extends State<HomeScreenStatefulWidget>
    with TickerProviderStateMixin {
  late AnimationController controller;
  late CurvedAnimation _animation;

  String getPrice() {
    Random rng = Random();
    var price = rng.nextInt(100);
    return "${price.toString()}.00 €";
  }

  void _onClick(PointerEvent details) {
    String price = getPrice();
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => PaymentScreen(
          price: price,
        ),
      ),
    );
  }

  @override
  void initState() {
    controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 5),
    )
      ..addListener(
        () {
          setState(() {});
        },
      )
      ..addStatusListener((status) {
        if (status == AnimationStatus.completed) {
          controller.reverse();
        } else if (status == AnimationStatus.dismissed) {
          controller.forward();
        }
      });

    _animation = CurvedAnimation(
      parent: controller,
      curve: Curves.linear,
    );

    controller.repeat(reverse: true);
    super.initState();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Listener(
          onPointerDown: _onClick,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  CircularProgressIndicator(
                    value: controller.value,
                    color: Colors.white,
                    strokeWidth: 3,
                    semanticsLabel: 'Circular progress indicator',
                  ),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
