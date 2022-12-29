import 'package:flutter/material.dart';
import 'package:tpe/app.dart';
import 'package:tpe/services/transaction_service.dart';

void showAuthModal() {
  TransactionService transactionService = TransactionService();
  BuildContext appContext = getContext();
  showModalBottomSheet(
    isScrollControlled: true,
    isDismissible: false,
    context: appContext,
    builder: (BuildContext context) {
      return Padding(
        padding:
            EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
        child: SingleChildScrollView(
          keyboardDismissBehavior: ScrollViewKeyboardDismissBehavior.onDrag,
          child: Padding(
            padding: const EdgeInsets.all(32.0),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                TextField(
                  onChanged: (value) {
                    transactionService.setPassword(value);
                  },
                  decoration: const InputDecoration(
                      labelText: 'Enter your password',
                      helperText: 'Let blank to register'),
                ),
                const SizedBox(height: 16.0),
                ElevatedButton(
                  child: const Text('Submit'),
                  onPressed: () {
                    transactionService.restart(appContext);
                    return Navigator.pop(context);
                  },
                ),
              ],
            ),
          ),
        ),
      );
    },
  );
}
