import 'package:flutter/widgets.dart';
import 'package:go_router/go_router.dart';

final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

// Navigate to a route, can be call from anywhere in the app and uses the main context
navigate(String routeName) {
  navigatorKey.currentContext!.go(routeName);
}
