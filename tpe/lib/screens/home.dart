import 'package:flutter/material.dart';

class PaymentTerminalApp extends StatelessWidget {
  const PaymentTerminalApp({super.key});

  static const String _title = 'Flutter Code Sample';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: _title,
      home: PaymentTerminalStatefulWidget(),
      theme: ThemeData(
          scaffoldBackgroundColor: Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class PaymentTerminalStatefulWidget extends StatefulWidget {
  const PaymentTerminalStatefulWidget({super.key});

  @override
  State<PaymentTerminalStatefulWidget> createState() =>
      _PaymentTerminalStatefulWidgetState();
}

class _PaymentTerminalStatefulWidgetState
    extends State<PaymentTerminalStatefulWidget> with TickerProviderStateMixin {
  late AnimationController controller;
  late CurvedAnimation _animation;

  void _onClick(PointerEvent details) {
    print("clicked");
  }

  @override
  void initState() {
    controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 5),
    )..forward();

    _animation = CurvedAnimation(
      parent: controller,
      curve: Curves.linear,
    )..addStatusListener((AnimationStatus status) {
        if (status == AnimationStatus.completed) print('completed');
      });

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
