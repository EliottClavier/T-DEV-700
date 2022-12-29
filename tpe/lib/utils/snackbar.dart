import 'package:flutter/material.dart';
import 'package:tpe/config/router/navigator.dart';

import 'package:go_router/go_router.dart';

void snackbarThenNavigate(BuildContext context, message, status, route) {
  showSnackBar(context, message, status, 1);
  if (route == null) return;
  Future.delayed(const Duration(seconds: 1), () {
    navigatorKey.currentContext!.go(route ?? '/');
  });
}

void showSnackBar(
    BuildContext context, String message, String status, int duration) {
  ScaffoldMessenger.of(context)
      .showSnackBar(getSnackBar(message, status, duration));
}

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

Color getBackgroundColor(String status) {
  if (status == "success") {
    return Colors.green;
  } else if (status == "error") {
    return Colors.red;
  } else {
    return Colors.blue;
  }
}
