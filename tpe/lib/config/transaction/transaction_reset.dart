import "package:tpe/config/router/navigator.dart";
import "package:tpe/app.dart";
import "package:tpe/services/transaction_service.dart";

TransactionService transactionService = TransactionService();

// Handle transaction reset event with error message
void onTransactionResetWithError([String message = "Error"]) async {
  if (message.isNotEmpty) {
    transactionService.setStatus(message);
  }
  navigate("/payment/error");
}

// Handle transaction reset event
void resetTransaction() async {
  transactionService.killTransaction();
  navigate("/");
  await Future.delayed(const Duration(seconds: 1));
  transactionService.restart(getContext());
}
