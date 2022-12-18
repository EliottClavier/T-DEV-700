import 'package:flutter_test/flutter_test.dart';

import 'package:tpe/screens/nfc_reader.dart';

void main() {
  testWidgets('NFC data formatter test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const NfcReaderScreen(price: "1.00"));

    NfcReaderScreenWidgetState nfcReaderScreenWidgetState =
        NfcReaderScreenWidgetState();

    // Set Data
    Map<String, dynamic> nfcData = {
      "isodep": {
        "identifier": [214, 62, 47, 69, 2, 251, 35]
      }
    };

    // Test Map<String, dynamic> to String data formatted great
    nfcReaderScreenWidgetState.setNfcData(nfcData);
    expect(nfcReaderScreenWidgetState.nfcDataString,
        "214-062-047-069-002-251-035");
  });
}
