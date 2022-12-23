import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:tpe/utils/navigator.dart';
import 'package:tpe/screens/home.dart';
import 'package:tpe/screens/nfc_reader.dart';
import 'package:tpe/screens/qr_code_reader.dart';
import 'package:tpe/screens/payment.dart';
import 'package:tpe/screens/payment_success.dart';
import 'package:tpe/screens/payment_error.dart';
import 'package:tpe/screens/payment_sending.dart';

// GoRouter configuration
final router = GoRouter(
  navigatorKey: navigatorKey,
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => const HomeScreen(),
    ),
    GoRoute(
      path: '/scan/qr-code',
      builder: (context, state) => const QrCodeReaderScreen(),
    ),
    GoRoute(
      path: '/scan/nfc',
      builder: (context, state) => const NfcReaderScreen(),
    ),
    GoRoute(
      path: '/payment',
      builder: (context, state) => const PaymentScreen(),
      routes: [
        GoRoute(
          path: 'sending',
          builder: (context, state) => const PaymentSendingScreen(),
        ),
        GoRoute(
          path: 'success',
          builder: (context, state) => const PaymentSuccessScreen(),
          pageBuilder: (context, state) => buildPageWithDefaultTransition<void>(
            context: context,
            state: state,
            child: const PaymentSuccessScreen(),
          ),
        ),
        GoRoute(
          path: 'error',
          builder: (context, state) => const PaymentErrorScreen(),
        ),
      ],
    ),
  ],
);

CustomTransitionPage buildPageWithDefaultTransition<T>({
  required BuildContext context,
  required GoRouterState state,
  required Widget child,
}) {
  return CustomTransitionPage<T>(
    key: state.pageKey,
    child: child,
    transitionsBuilder: (context, animation, secondaryAnimation, child) =>
        FadeTransition(opacity: animation, child: child),
  );
}
