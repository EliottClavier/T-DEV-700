import 'package:flutter/material.dart';

import 'package:tpe/screens/payment_success.dart';
import 'package:tpe/screens/payment_error.dart';
import 'package:tpe/connectors/bank.dart';

class PaymentSendingScreen extends StatelessWidget {
  final String price;
  final String paymentMethod;
  final String paymentData;

  const PaymentSendingScreen(
      {super.key,
      required this.price,
      required this.paymentMethod,
      required this.paymentData});

  static const String _title = 'Sending Payment';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: PaymentSendingScreenStatefulWidget(
        price: price,
        paymentMethod: paymentMethod,
        paymentData: paymentData,
      ),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentSendingScreenStatefulWidget extends StatefulWidget {
  const PaymentSendingScreenStatefulWidget(
      {super.key,
      required this.price,
      required this.paymentMethod,
      required this.paymentData});

  final String price;
  final String paymentMethod;
  final String paymentData;

  @override
  State<PaymentSendingScreenStatefulWidget> createState() =>
      _PaymentSendingScreenStatefulWidgetState();
}

class _PaymentSendingScreenStatefulWidgetState
    extends State<PaymentSendingScreenStatefulWidget>
    with TickerProviderStateMixin {
  late AnimationController controller;
  late CurvedAnimation _animation;

  @override
  void initState() {
    super.initState();
    controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 3),
    )
      ..addListener(
        () {
          setState(() {});
        },
      )
      ..addStatusListener(
        (status) {
          if (status == AnimationStatus.completed) {
            controller.reverse();
          } else if (status == AnimationStatus.dismissed) {
            controller.forward();
          }
        },
      );

    _animation = CurvedAnimation(
      parent: controller,
      curve: Curves.linear,
    );

    controller.repeat(reverse: true);

    //Simulate request to bank
    Future.delayed(const Duration(milliseconds: 2000), () {
      sendData();
    });
  }

  @override
  void dispose() {
    super.dispose();
  }

  void sendData() async {
    var response = widget.paymentMethod == "qr_code"
        ? await sendQrCodeData(widget.paymentData)
        : await sendNfcData(widget.paymentData);
    Widget screen = const PaymentErrorScreen();
    if (response) {
      screen = PaymentSuccessScreen(
        price: widget.price,
      );
    }
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => screen,
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
                CircularProgressIndicator(
                  value: controller.value,
                  color: Colors.white,
                  strokeWidth: 3,
                  semanticsLabel: 'Circular progress indicator',
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
