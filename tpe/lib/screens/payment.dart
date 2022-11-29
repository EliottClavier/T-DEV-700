import 'dart:math';
import 'package:flutter/material.dart';

import 'package:tpe/screens/payment_success.dart';
import 'package:tpe/screens/payment_error.dart';
import 'package:tpe/screens/nfc_reader.dart';

class PaymentScreen extends StatelessWidget {
  const PaymentScreen({super.key, required this.price});

  final String price;
  static const String _title = 'Payment method';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: PaymentScreenStatefulWidget(
        price: price,
      ),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentScreenStatefulWidget extends StatefulWidget {
  const PaymentScreenStatefulWidget({super.key, required this.price});

  final String price;

  @override
  State<PaymentScreenStatefulWidget> createState() =>
      _PaymentScreenStatefulWidgetState();
}

class _PaymentScreenStatefulWidgetState
    extends State<PaymentScreenStatefulWidget> {
  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _onPaymentSent() {
    Random random = Random();
    StatelessWidget screen = random.nextBool()
        ? PaymentSuccessScreen(
            price: widget.price,
          )
        : const PaymentErrorScreen();
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => screen,
      ),
    );
  }

  void _onNfcSelected() {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const NfcReaderScreen(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text(
                  "Montant: ${widget.price}",
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontWeight: FontWeight.w700,
                    color: Colors.white,
                    fontSize: 32,
                    letterSpacing: 0.02,
                    height: 1.2,
                  ),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                IconButton(
                  icon: Image.asset('assets/img/qr_code.png'),
                  iconSize: 300,
                  onPressed: () {
                    _onPaymentSent();
                  },
                )
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                IconButton(
                  icon: Image.asset('assets/img/nfc.png'),
                  iconSize: 300,
                  onPressed: () {
                    _onNfcSelected();
                  },
                )
              ],
            ),
          ],
        ),
      ),
    );
  }
}
