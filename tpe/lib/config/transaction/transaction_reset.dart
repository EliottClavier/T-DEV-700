import "package:tpe/services/transaction_service.dart";
import "package:tpe/config/router/navigator.dart";
import "package:tpe/app.dart";
import "package:provider/provider.dart";

TransactionService transactionService = TransactionService();

void onTransactionReset([String message = ""]) async {
  if (message.isNotEmpty) {
    transactionService.setStatus(message);
  }
  navigate("/payment/error");
  resetTransaction();
}

void resetTransaction() async {
  transactionService.killTransaction();
  navigate("/");
  await Future.delayed(const Duration(seconds: 1));
  transactionService.restart(getContext());
}
