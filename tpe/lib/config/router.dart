import 'package:go_router/go_router.dart';

import 'package:tpe/screens/home.dart';
import 'package:tpe/screens/nfc_reader.dart';
import 'package:tpe/screens/qr_code_reader.dart';
import 'package:tpe/screens/payment.dart';
import 'package:tpe/screens/payment_success.dart';
import 'package:tpe/screens/payment_error.dart';
import 'package:tpe/screens/payment_sending.dart';

// GoRouter configuration
final router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => const HomeScreen(),
    ),
    GoRoute(
      path: '/scan/qr-code',
      builder: (context, state) => const QrCodeReaderScreen(
        price: "Test",
      ),
    ),
    GoRoute(
      path: '/scan/nfc',
      builder: (context, state) => const NfcReaderScreen(
        price: "Test",
      ),
    ),
    GoRoute(
      path: '/payment',
      builder: (context, state) => const PaymentScreen(
        price: "Test",
      ),
      routes: [
        GoRoute(
          path: 'sending/:method',
          builder: (context, state) => PaymentSendingScreen(
            price: "Test",
            paymentMethod: state.params['method']!,
            paymentData: "Test",
          ),
        ),
        GoRoute(
          path: 'success',
          builder: (context, state) => const PaymentSuccessScreen(
            price: "Test",
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
