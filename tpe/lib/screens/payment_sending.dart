import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class PaymentSendingScreen extends StatelessWidget {
  const PaymentSendingScreen({super.key});

  static const String _title = 'Sending Payment';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: const PaymentSendingScreenStatefulWidget(),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentSendingScreenStatefulWidget extends StatefulWidget {
  const PaymentSendingScreenStatefulWidget({super.key});

  @override
  State<PaymentSendingScreenStatefulWidget> createState() =>
      _PaymentSendingScreenStatefulWidgetState();
}

class _PaymentSendingScreenStatefulWidgetState
    extends State<PaymentSendingScreenStatefulWidget> {
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
              children: const [
                Text(
                  "Paiement en cours",
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 30,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Image.asset("assets/gif/payment_loader.gif",
                    width: 200, height: 200),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
