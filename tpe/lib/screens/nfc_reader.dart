import 'package:nfc_manager/nfc_manager.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:go_router/go_router.dart';

import 'package:tpe/screens/payment.dart';
import 'package:tpe/services/transaction_service.dart';
import 'package:tpe/utils/snackbar.dart';

class NfcReaderScreen extends StatelessWidget {
  const NfcReaderScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "NFC Reader",
      home: NfcReaderScreenWidget(),
      theme: ThemeData(
          scaffoldBackgroundColor: const Color(0xFF03045F),
          primarySwatch: Colors.blue,
          fontFamily: "Montserrat"),
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
  final TransactionService transactionService = TransactionService();
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
    var snackBarMessage =
        nfcDataString.isNotEmpty ? "Scan NFC réussi" : "Erreur de scan";
    showSnackBar(context, snackBarMessage, "success", 1);
    Future.delayed(const Duration(milliseconds: 1000), () {
      transactionService.payWithNfc(nfcDataString);
    });
  }

  void onNfcDiscovered(NfcTag tag) {
    setNfcData(tag.data);
  }

  void _initNfc() async {
    try {
      await widget.nfcManager.startSession(onDiscovered: (NfcTag tag) {
        setNfcData(tag.data);
        return Future.value();
      });
    } on PlatformException catch (e) {
      showSnackBar(context, "NFC not available for device", "error", 2);
      Future.delayed(const Duration(milliseconds: 2000), () {
        _disposeNfc();
        context.go("/payment");
      });
    }
  }

  void _disposeNfc() async {
    widget.nfcManager.stopSession();
  }

  void _onBackButtonPressed() {
    dispose();
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => const PaymentScreen(),
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
          children: <Widget>[
            Image.asset("assets/gif/animation_nfc.gif"),
            const Text(
              "Veuillez scanner votre carte",
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
