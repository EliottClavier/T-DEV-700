import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tpe/services/transaction_service.dart';
import 'package:tpe/config/transaction/transaction_reset.dart';

class PaymentErrorScreen extends StatelessWidget {
  const PaymentErrorScreen({super.key});

  static const String _title = 'Payment method';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: const PaymentErrorScreenStatefulWidget(),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFFBE1E1E),
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentErrorScreenStatefulWidget extends StatefulWidget {
  const PaymentErrorScreenStatefulWidget({super.key});

  @override
  State<PaymentErrorScreenStatefulWidget> createState() =>
      _PaymentErrorScreenStatefulWidgetState();
}

class _PaymentErrorScreenStatefulWidgetState
    extends State<PaymentErrorScreenStatefulWidget> {
  TransactionService transactionService = TransactionService();
  late String status;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    initStatus();
  }

  @override
  void initState() {
    super.initState();
  }

  void initStatus() {
    status = Provider.of<TransactionService>(context, listen: true).getStatus();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _onBackHome() {
    resetTransaction();
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
              children: const <Widget>[
                Image(image: AssetImage("assets/img/x-circle.png")),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    const Text(
                      "Payment refused",
                      textAlign: TextAlign.center,
                      style: TextStyle(
                        fontWeight: FontWeight.w400,
                        color: Colors.white,
                        fontSize: 15,
                        letterSpacing: 0.02,
                        height: 1.2,
                      ),
                    ),
                    Text(
                      status,
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontWeight: FontWeight.w300,
                        color: Colors.white,
                        fontSize: 15,
                        letterSpacing: 0.02,
                        height: 1.2,
                      ),
                    ),
                  ],
                )
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                OutlinedButton(
                    onPressed: _onBackHome,
                    style: ButtonStyle(
                      minimumSize:
                          MaterialStateProperty.all(const Size(250, 50)),
                    ),
                    child: const Text(
                      "Revenir à l'accueil",
                      style: TextStyle(
                        fontWeight: FontWeight.w700,
                        color: Colors.white,
                        fontSize: 15,
                        height: 1.2,
                      ),
                    ))
              ],
            ),
          ],
        ),
      ),
    );
  }
}
