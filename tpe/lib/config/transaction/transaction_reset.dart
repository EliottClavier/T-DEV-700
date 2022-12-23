import "package:tpe/services/transaction_service.dart";
import "package:tpe/config/router/navigator.dart";

TransactionService transactionService = TransactionService();

void onTransactionReset([String message = ""]) async {
  if (message.isNotEmpty) {
    transactionService.setStatus(message);
  }
  navigate("/payment/error");
  await Future.delayed(const Duration(seconds: 2));
  await transactionService.killTransaction();
  navigate("/");
  transactionService.init(navigatorKey.currentContext!);
}
