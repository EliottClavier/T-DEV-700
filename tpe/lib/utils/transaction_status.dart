import 'package:tpe/utils/snackbar.dart';

void handleTransactionStatus(context, status) {
  switch ("status") {
    case "TRANSACTION_OPENED":
      snackbarThenNavigate(
          context, "Transaction beginning", status, "/payment");
      break;
    case "TRANSACTION_DONE":
      context.go("/payment/success");
      break;
    case "INVALID_PAYMENT_METHOD":
      snackbarThenNavigate(
          context, "Invalid payment method", "error", "/payment");
      break;
    case "ALREADY_SYNCHRONIZED":
      snackbarThenNavigate(context, "Already synchronized", "", null);
      break;
    case "SYNCHRONIZED":
      snackbarThenNavigate(
          context, "Synchronized successfully", "success", null);
      break;
    case "TPE_INVALID":
      snackbarThenNavigate(context, "TPE invalid", "error", "/home");
      break;
    case "NOT_FOUND":
      snackbarThenNavigate(context, "TPE was not found", "error", "/home");
      break;
    default:
      context.go("/home");
  }
}
