import 'package:tpe/utils/snackbar.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:tpe/utils/context.dart';

String handleTransactionStatus(
    BuildContext context, Map<String, dynamic> body) {
  String status = body['type'];
  String message = body['message'];
  switch (status) {
    case "TRANSACTION_OPENED":
      snackbarThenNavigate(context, message, "success", "/payment");
      break;
    case "TRANSACTION_DONE":
      context.go("/payment/success");
      break;
    case "INVALID_PAYMENT_METHOD":
      snackbarThenNavigate(context, message, "error", "/payment");
      break;
    case "ALREADY_SYNCHRONIZED":
      snackbarThenNavigate(context, message, "", null);
      break;
    case "SYNCHRONIZED":
      snackbarThenNavigate(context, message, "success", null);
      break;
    case "TPE_INVALID":
      snackbarThenNavigate(context, message, "error", "/");
      break;
    case "NOT_FOUND":
      snackbarThenNavigate(context, message, "error", "/");
      break;
    default:
      context.go('/');
  }

  return message;
}
