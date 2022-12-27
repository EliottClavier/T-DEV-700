import 'package:flutter/material.dart';

class Separation extends StatelessWidget {
  const Separation({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width - 40,
      decoration: const BoxDecoration(
        border: Border(
          top: BorderSide(width: 1, color: Color.fromARGB(255, 184, 72, 96)),
        ),
      ),
    );
  }
}
