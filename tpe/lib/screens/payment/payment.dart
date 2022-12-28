import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:tpe/services/transaction_service.dart';

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
  TransactionService transactionService = TransactionService();

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _onNfcSelected() {
    context.go("/scan/nfc");
  }

  void _onQrCodeSelected() {
    context.go("/scan/qr-code");
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
                "Amount: ${transactionService.getAmount()}",
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
                      "Pay by check",
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
                        "Pay by NFC",
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
