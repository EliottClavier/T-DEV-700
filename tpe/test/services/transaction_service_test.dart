import 'package:flutter_test/flutter_test.dart';
import 'package:tpe/services/transaction_service.dart';
import 'package:mockito/mockito.dart';
import 'package:tpe/utils/enums/transaction_type.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'package:http/http.dart' as http;
import 'package:tpe/config/security/token.dart';
import 'package:tpe/config/transaction/transaction_status.dart';
import 'package:tpe/config/environment/index.dart';
import 'package:tpe/config/security/android_id.dart';
import 'package:tpe/utils/amount.dart';
import 'package:tpe/utils/enums/transaction_type.dart';
import 'package:tpe/config/router/navigator.dart';
import 'package:tpe/utils/auth_modal.dart';

class MockTransactionService extends Mock implements TransactionService {}

class MockStompClient extends Mock implements StompClient {}

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  TransactionService transactionService = TransactionService();
  MockTransactionService mockTransactionService = MockTransactionService();
  MockStompClient mockStompClient = MockStompClient();

  setUp(() {
    mockTransactionService = MockTransactionService();
    transactionService = TransactionService();
    StompClient clientTest =
        StompClient(config: transactionService.getClientConfig());
    transactionService.setClient(clientTest);
  });

  test('setStatus() sets the status correctly', () {
    transactionService.setStatus('Connected');
    expect(transactionService.getStatus(), 'Connected');
  });

  test('setAmount() sets the amount correctly', () {
    transactionService.setAmount(20.0);
    expect(transactionService.getAmount(), '20.0 €');
  });

  test('resetAttributes() resets the attributes correctly', () {
    transactionService.resetAttributes();
    expect(transactionService.getStatus(), 'Disconnected.');
    expect(transactionService.getAmount(), '0.0 €');
    expect(transactionService.getTransactionType(),
        TransactionType.NFC.toString());
    expect(transactionService.getPaymentId(), '');
    expect(transactionService.getSessionId(), '');
  });

  test('getClientConfig() returns the correct StompConfig', () {
    StompConfig config = transactionService.getClientConfig();
    const String API_HTTP_PROTOCOL_TEST = 'wss';
    expect(
        config.url.startsWith(
            '$API_HTTP_PROTOCOL_TEST://$API_URL/websocket-manager/secured/tpe/socket'),
        true);
    expect(config.stompConnectHeaders, {'Authorization': 'Bearer '});
    expect(config.webSocketConnectHeaders, {
      'Authorization': 'Bearer ',
      'Connection': 'upgrade',
      'Upgrade': 'WebSocket'
    });
  });

  test('createClient() creates a new StompClient', () {
    StompClient client = transactionService.createClient();
    expect(client, isNotNull);
    expect(client, isA<StompClient>());
  });

  test('setClient() sets the client correctly', () {
    transactionService.setClient(mockStompClient);
    expect(transactionService.getClient(), mockStompClient);
  });

  test(
      'onOpenTransaction should set the amount and status and call notifyListeners',
      () {
    double amount = 123.45;

    // Act
    transactionService.onOpenTransaction(amount);

    // Assert
    expect(transactionService.getAmount(), '123.45 €');
    expect(transactionService.getStatus(), 'Transaction opened');
  });
}
