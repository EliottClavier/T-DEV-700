import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:tpe/services/transaction_service.dart';
import 'package:tpe/screens/home.dart';

void main() {
  testWidgets(
      'HomeScreen should return a MaterialApp with the correct title and home',
      (WidgetTester tester) async {
    // Arrange
    final TransactionService transactionService = TransactionService();
    final Widget homeScreenStatefulWidget = MaterialApp(
      home: ChangeNotifierProvider<TransactionService>(
        create: (_) => transactionService,
        child: const HomeScreenStatefulWidget(),
      ),
    );

    // Act
    await tester.pumpWidget(homeScreenStatefulWidget);

    // Assert
    final Scaffold scaffold = tester.widget<Scaffold>(find.byType(Scaffold));
    expect(scaffold.resizeToAvoidBottomInset, isTrue);
    final Column column = tester.widget<Column>(find.byType(Column));
    expect(column.mainAxisAlignment, MainAxisAlignment.center);
    final Row row = tester.widgetList<Row>(find.byType(Row)).first;
    expect(row.mainAxisAlignment, MainAxisAlignment.center);
    final Image image = tester.widgetList<Image>(find.byType(Image)).first;
    expect(image.width, 300);
    expect(image.height, 300);
  });
}
