import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

Future<Album> fetchAlbum() async {
  final response = await http
      .get(Uri.parse('https://jsonplaceholder.typicode.com/albums/2'));
  if (response.statusCode == 200) {
    return Album.fromJson(jsonDecode(response.body));
  } else {
    throw Exception('Failed to load album');
  }
}

class Album {
  final int userId;
  final int id;
  final String title;

  const Album({
    required this.userId,
    required this.id,
    required this.title,
  });

  factory Album.fromJson(Map<String, dynamic> json) {
    return Album(
      userId: json['userId'],
      id: json['id'],
      title: json['title'],
    );
  }
}

void main() => runApp(const MyApp());

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late Future<Album> futureAlbum;

  @override
  void initState() {
    super.initState();
    futureAlbum = fetchAlbum();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fetch Data Example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Fetch Data Example'),
        ),
        body: Center(
          child: FutureBuilder<Album>(
            future: futureAlbum,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                return Text(snapshot.data!.title);
              } else if (snapshot.hasError) {
                return Text('${snapshot.error}');
              }

              // By default, show a loading spinner.
              return const CircularProgressIndicator();
            },
          ),
        ),
      ),
    );
  }
}

/*

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class ApiConnexionWidget extends StatefulWidget {
  const ApiConnexionWidget({super.key});

  @override
  State<ApiConnexionWidget> createState() => ApiConnexionState();
}

class ApiConnexionState extends State<ApiConnexionWidget> {
  late Future<Text> apiResponse;
  String? apiResponseText;

  @override
  void initState() {
    super.initState();
    apiResponse = api();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: ElevatedButton(
            onPressed: () {
              FutureBuilder<Text>(
                future: apiResponse,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    return Text('$apiResponseText');
                  } else if (snapshot.hasError) {
                    return Text('${snapshot.error}');
                  }
                  return const CircularProgressIndicator();
                },
              );
            },
            child: const Text('Tentative de connexion'),
          ),
        ),
      ),
    );
  }

  Future<Text> api() async {
    final response = await http.get(Uri.parse('http://10.0.2.2:8080'));
    if (response.statusCode == 200) {
      print('OK !');
      setState(() {
        apiResponseText = response.body;
      });
      return const Text('Connexion effectuée !');
    } else {
      throw Exception('Failed');
    }
  }
}
*/