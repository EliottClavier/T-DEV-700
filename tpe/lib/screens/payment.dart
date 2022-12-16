import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_redux/flutter_redux.dart';

import 'package:tpe/screens/payment_success.dart';
import 'package:tpe/screens/payment_error.dart';
import 'package:tpe/screens/nfc_reader.dart';
import 'package:tpe/screens/qr_code_reader.dart';
import 'package:tpe/utils/price.dart';

class PaymentScreen extends StatelessWidget {
  const PaymentScreen({super.key});

  static const String _title = 'Payment method';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: const PaymentScreenStatefulWidget(),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentScreenStatefulWidget extends StatefulWidget {
  const PaymentScreenStatefulWidget({super.key});

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
    dispose();
    Random random = Random();
    StatelessWidget screen = random.nextBool()
        ? const PaymentSuccessScreen()
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

  void _onQrCodeSelected() {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const QrCodeReaderScreen(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Padding(
      padding: const EdgeInsets.all(20.0),
      child: Padding(
        padding: const EdgeInsets.only(top: 35),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              Text(
                "Montant: ${getAmount()}",
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontWeight: FontWeight.w700,
                  color: Colors.white,
                  fontSize: 32,
                  letterSpacing: 0.02,
                  height: 1.2,
                ),
              ),
              Stack(
                clipBehavior: Clip.none,
                alignment: Alignment.center,
                children: <Widget>[
                  Container(
                    margin: const EdgeInsets.only(bottom: 20),
                    child: IconButton(
                      icon: Image.asset('assets/img/qr_code.png'),
                      iconSize: 300,
                      onPressed: () {
                        _onQrCodeSelected();
                      },
                    ),
                  ),
                  const Positioned(
                    bottom: 45,
                    child: Text(
                      "Payer par chèque",
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontWeight: FontWeight.w700,
                        color: Colors.white,
                        fontSize: 25,
                        letterSpacing: 0.02,
                        height: 1.2,
                      ),
                    ),
                  ),
                ],
              ),
              Stack(
                  clipBehavior: Clip.none,
                  alignment: Alignment.center,
                  children: <Widget>[
                    Container(
                      margin: const EdgeInsets.only(bottom: 20),
                      child: IconButton(
                        icon: Image.asset('assets/img/nfc.png'),
                        iconSize: 300,
                        onPressed: () {
                          _onNfcSelected();
                        },
                      ),
                    ),
                    const Positioned(
                      bottom: 45,
                      child: Text(
                        "Payer par NFC",
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          fontWeight: FontWeight.w700,
                          color: Colors.white,
                          fontSize: 25,
                          letterSpacing: 0.02,
                          height: 1.2,
                        ),
                      ),
                    ),
                  ]),
            ],
          ),
        ),
      ),
    ));
  }
}
