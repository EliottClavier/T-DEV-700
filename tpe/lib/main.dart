import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tpe/app.dart';
import 'package:tpe/services/bank_service.dart';

void main() => {
      runApp(
        ChangeNotifierProvider(
          create: (context) => BankService(),
          child: App(),
        ),
      ),
    };
