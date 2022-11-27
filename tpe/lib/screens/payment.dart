import 'package:flutter/material.dart';

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

/// AnimationControllers can be created with `vsync: this` because of TickerProviderStateMixin.
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
                  "Montant: 0.00 €",
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                const Image(image: AssetImage("assets/img/qr_code.png")),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                const Image(image: AssetImage("assets/img/nfc.png")),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
