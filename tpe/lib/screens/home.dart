import 'dart:math';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tpe/services/bank_service.dart';
import 'package:go_router/go_router.dart';

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

class _HomeScreenStatefulWidgetState extends State<HomeScreenStatefulWidget> {
  BankService bankService = BankService();
  late bool isConnectedToApi;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _initBank();
  }

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _initBank() async {
    isConnectedToApi = _isConnectedToApi();
  }

  bool _isConnectedToApi() {
    bool response =
        Provider.of<BankService>(context, listen: true).isConnectedToApi;
    if (response) {
      onConnectedToApi();
    }
    return response;
  }

  void onConnectedToApi() {
    print("Connected to API");
  }

  String getPrice() {
    Random rng = Random();
    var price = rng.nextInt(100);
    return "${price.toString()}.00 €";
  }

  void _onClick(PointerEvent details) {
    String price = getPrice();
    print("Price: $price");
    bankService.printStatus();
    context.go("/payment");
    /* Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => PaymentScreen(
          price: price,
        ),
      ),
    ); */
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
                  Image.asset("assets/gif/home_loader.gif",
                      width: 300, height: 300),
                ],
              ),
              Text("$isConnectedToApi"),
            ],
          ),
        ),
      ),
    );
  }
}
