import 'package:tpe/services/transaction_service.dart';
import 'package:tpe/config/transaction/transaction_reset.dart';

void onTransactionCanceled() async {
  TransactionService transactionService = TransactionService();
  try {
    transactionService.sendKillTransaction();
  } catch (e) {
    onTransactionResetWithError(e.toString());
  }
}
