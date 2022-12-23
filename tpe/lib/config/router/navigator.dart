import 'package:flutter/widgets.dart';
import 'package:go_router/go_router.dart';

final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

navigate(String routeName) {
  navigatorKey.currentContext!.go(routeName);
}
