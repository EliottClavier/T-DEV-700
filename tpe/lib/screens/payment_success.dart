import 'package:flutter/material.dart';
import 'package:tpe/screens/home.dart';

class PaymentSuccessScreen extends StatelessWidget {
  const PaymentSuccessScreen({super.key, required this.price});

  static const String _title = 'Payment method';
  final String price;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: PaymentSuccessScreenStatefulWidget(
        price: price,
      ),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF1EBE4B),
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentSuccessScreenStatefulWidget extends StatefulWidget {
  const PaymentSuccessScreenStatefulWidget({super.key, this.price});

  final String? price;

  @override
  State<PaymentSuccessScreenStatefulWidget> createState() =>
      _PaymentSuccessScreenStatefulWidgetState();
}

class _PaymentSuccessScreenStatefulWidgetState
    extends State<PaymentSuccessScreenStatefulWidget> {
  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _onBackHome() {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const HomeScreen(),
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
              children: const <Widget>[
                Image(image: AssetImage("assets/img/check-circle.png")),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    const Text(
                      "Paiement réalisé avec succès",
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
                      "Votre paiement de ${widget.price} a bien été réalisé",
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
