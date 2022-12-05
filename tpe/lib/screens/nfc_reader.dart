import 'dart:ffi';

import 'package:nfc_manager/nfc_manager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:tpe/screens/payment.dart';

class NfcReaderScreen extends StatelessWidget {
  const NfcReaderScreen({super.key, required this.price});

  final String price;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'NFC Reader',
      home: NfcReaderScreenWidget(price: price),
      theme: ThemeData(
        scaffoldBackgroundColor: const Color(0xFF03045F),
        primarySwatch: Colors.blue,
        fontFamily: "Montserrat",
      ),
    );
  }
}

class NfcReaderScreenWidget extends StatefulWidget {
  NfcReaderScreenWidget({super.key, required this.price});

  final NfcManager nfcManager = NfcManager.instance;
  final String price;

  @override
  State<NfcReaderScreenWidget> createState() => NfcReaderScreenWidgetState();
}

class NfcReaderScreenWidgetState extends State<NfcReaderScreenWidget> {
  String nfcDataString = "";

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

  String getFormatedDataItem(int item) {
    return item.toString().length == 2
        ? "0${item.toString()}"
        : item.toString().length == 1
            ? "00${item.toString()}"
            : item.toString();
  }

  String getFormatedDataFromNfcData(List<int> nfcData) {
    var result = "";
    for (var dataItem in nfcData) {
      result += result.isEmpty
          ? getFormatedDataItem(dataItem)
          : "-${getFormatedDataItem(dataItem)}";
    }
    return result;
  }

  void setNfcData(Map<String, dynamic> nfcData) {
    List<int> identifier = [];
    nfcData.forEach((key, value) {
      if (key == "isodep" && value["identifier"] is List<int>) {
        identifier = (value["identifier"]);
      }
    });
    nfcDataString = getFormatedDataFromNfcData(identifier);
  }

  void _initNfc() async {
    try {
      await widget.nfcManager.startSession(onDiscovered: (NfcTag tag) {
        setNfcData(tag.data);
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

  void _onBackButtonPressed() {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => PaymentScreen(
          price: widget.price,
        ),
      ),
    );
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
              "Paiement NFC",
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
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          _onBackButtonPressed();
        },
        backgroundColor: Colors.white,
        child: IconButton(
            onPressed: _onBackButtonPressed,
            color: Colors.white,
            icon: Image.asset("assets/img/arrow-left.png")),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.startTop,
    );
  }
}