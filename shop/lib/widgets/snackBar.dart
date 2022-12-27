import 'package:flutter/material.dart';

/* This function displays a snackbar with the given message, status, and duration.
The context parameter is the build context of the widget where the snackbar will be displayed.
The message parameter is the text to be displayed in the snackbar.
The status parameter determines the background color of the snackbar.
The duration parameter determines how long the snackbar will be displayed.*/
void showSnackBar(
    BuildContext context, String message, String status, int duration) {
  ScaffoldMessenger.of(context)
      .showSnackBar(getSnackBar(message, status, duration));
}

/* This function creates a snackbar with the given message, status, and duration.
The message parameter is the text to be displayed in the snackbar.
The status parameter determines the background color of the snackbar.
The duration parameter determines how long the snackbar will be displayed.*/
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

/* This function returns the background color of the snackbar based on the status parameter.
If the status is "success", the background color will be green.
If the status is "error", the background color will be red.
Otherwise, the background color will be blue.*/
Color getBackgroundColor(String status) {
  if (status == "success") {
    return Colors.green;
  } else if (status == "error") {
    return Colors.red;
  } else {
    return Colors.blue;
  }
}
