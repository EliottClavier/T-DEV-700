import 'package:nfc_manager/nfc_manager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class NfcReaderScreen extends StatelessWidget {
  const NfcReaderScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'NFC Reader',
      home: NfcReaderScreenWidget(),
      theme: ThemeData(
        scaffoldBackgroundColor: const Color(0xFF03045F),
        primarySwatch: Colors.blue,
        fontFamily: "Montserrat",
      ),
    );
  }
}

class NfcReaderScreenWidget extends StatefulWidget {
  NfcReaderScreenWidget({super.key});

  final NfcManager nfcManager = NfcManager.instance;

  @override
  State<NfcReaderScreenWidget> createState() => NfcReaderScreenWidgetState();
}

class NfcReaderScreenWidgetState extends State<NfcReaderScreenWidget> {
  @override
  void initState() {
    super.initState();
    _initNfc();
  }

  @override
  void dispose() {
    super.dispose();
    _disposeNfc();
  }

  void _initNfc() async {
    try {
      await widget.nfcManager.startSession(onDiscovered: (NfcTag tag) {
        print('Discovered tag: ${tag.data}');
        return Future<void>.value();
      });
    } on PlatformException catch (e) {
      print('Error: $e');
    }
  }

  void _disposeNfc() async {
    try {
      await widget.nfcManager.stopSession();
    } on PlatformException catch (e) {
      print('Error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: const <Widget>[
            Text(
              'NFC Reader',
              style: TextStyle(
                color: Colors.white,
                fontSize: 30,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              'Place your phone near a NFC tag',
              style: TextStyle(
                color: Colors.white,
                fontSize: 20,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
