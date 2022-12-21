import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tpe/app.dart';
import 'package:tpe/services/transaction_service.dart';

void main() => {
      runApp(
        ChangeNotifierProvider(
          create: (context) => TransactionService(),
          child: const App(),
        ),
      ),
    };
