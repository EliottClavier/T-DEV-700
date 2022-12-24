import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tpe/config/router/router.dart';
import 'package:tpe/services/transaction_service.dart';
// ignore: unused_import
import 'package:go_router/go_router.dart';

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Payment Terminal',
      home: const Scaffold(
        body: AppWidget(),
      ),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
    );
  }
}

class AppWidget extends StatefulWidget {
  const AppWidget({super.key});

  @override
  State<AppWidget> createState() => _AppWidgetState();
}

class _AppWidgetState extends State<AppWidget> {
  final TransactionService transactionService = TransactionService();
  static const String _title = 'Payment Terminal';

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
    transactionService.init(context);
    setContext(context);
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

late BuildContext _context;

void setContext(BuildContext context) {
  _context = context;
}

BuildContext getContext() {
  return _context;
}
