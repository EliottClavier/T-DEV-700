import 'package:flutter/material.dart';
import 'package:tpe/config/router.dart';

class App extends StatelessWidget {
  const App({super.key});

  static const String _title = 'Payment Terminal Home';

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: _title,
      routerConfig: router,
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}
