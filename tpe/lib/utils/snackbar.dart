import 'package:flutter/material.dart';
import 'package:tpe/config/router/navigator.dart';

import 'package:go_router/go_router.dart';

// Navigates to the given route and shows a snackbar with the given message and status
void snackbarThenNavigate(BuildContext context, message, status, route) {
  showSnackBar(context, message, status, 1);
  if (route == null) return;
  Future.delayed(const Duration(seconds: 1), () {
    navigatorKey.currentContext!.go(route ?? '/');
  });
}

// Shows a snackbar with the given message and status
void showSnackBar(
    BuildContext context, String message, String status, int duration) {
  ScaffoldMessenger.of(context)
      .showSnackBar(getSnackBar(message, status, duration));
}

// Returns a snackbar with the given message and status
SnackBar getSnackBar(String message, String status, int duration) {
  Color backgroundColor = getBackgroundColor(status);

  return SnackBar(
    content: Text(
      message,
      style: const TextStyle(
        color: Colors.white,
      ),
    ),
    duration: Duration(seconds: duration),
    elevation: 25,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.all(
        Radius.circular(8),
      ),
    ),
    backgroundColor: backgroundColor,
  );
}

// Returns the background color of the snackbar based on the status
Color getBackgroundColor(String status) {
  if (status == "success") {
    return Colors.green;
  } else if (status == "error") {
    return Colors.red;
  } else {
    return Colors.blue;
  }
}
