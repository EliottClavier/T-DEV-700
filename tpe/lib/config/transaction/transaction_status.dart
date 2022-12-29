import 'package:tpe/utils/snackbar.dart';
import 'package:flutter/material.dart';
import 'package:tpe/config/router/navigator.dart';
import 'package:tpe/services/transaction_service.dart';
import 'package:tpe/config/transaction/transaction_reset.dart';

TransactionService transactionService = TransactionService();

String handleTransactionStatus(
    BuildContext context, Map<String, dynamic> body) {
  transactionService = TransactionService();
  String status = body['type'];
  String message = body['message'];
  switch (status) {
    case "TRANSACTION_OPENED":
      transactionService.setAmount(body['amount']);
      showSnackBar(context, message, "context", 1);
      navigate("/payment");
      break;
    case "TRANSACTION_DONE":
      transactionService.setStatus(message);
      navigate("/payment/success");
      break;
    case "INVALID_PAYMENT_METHOD":
      transactionService.setStatus(message);
      snackbarThenNavigate(context, message, "error", "/payment");
      break;
    case "ALREADY_SYNCHRONIZED":
      transactionService.setStatus(message);
      snackbarThenNavigate(context, message, "", null);
      break;
    case "SYNCHRONIZED":
      transactionService.setStatus(message);
      snackbarThenNavigate(context, message, "success", null);
      break;
    case "TPE_INVALID":
      snackbarThenNavigate(context, message, "error", "/");
      break;
    case "NOT_FOUND":
      snackbarThenNavigate(context, message, "error", "/");
      break;
    case "SUCCESS":
      transactionService.setStatus(message);
      navigate("/payment/success");
      break;
    case "RESET":
      transactionService.setStatus(message);
      onTransactionResetWithError(message);
      break;
    case "LOST_CONNECTION":
      transactionService.setStatus("Lost server connection.");
      onTransactionResetWithError(message);
      break;
    case "TRANSACTION_TIMED_OUT":
      transactionService.setStatus(message);
      onTransactionResetWithError(message);
      break;
    default:
      onTransactionResetWithError(message);
  }

  return message;
}
