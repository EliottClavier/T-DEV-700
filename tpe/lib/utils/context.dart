import 'package:flutter/material.dart';
import 'package:tpe/config/router.dart';

BuildContext? getCurrentContext() {
  BuildContext? currentContext =
      router.routerDelegate.navigatorKey.currentContext;
  return currentContext;
}
