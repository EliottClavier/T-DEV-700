import 'package:shop/config/index.dart';

class Token {
  // The token is a string representing an authorization token
  String token = "";
  // The url is a static constant string representing the URL of the API
  static const String url = API_URL;

  // Constructor for Token class taking a required token parameter
  Token({required this.token});

  // Constructor for Token class taking a JSON map and creating a Token instance from it
  Token.fromJson(Map<String, dynamic> json) {
    token = json['token'];
  }

  // Method to convert a Token instance to a JSON map
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['token'] = token;
    return data;
  }
}

class Response {
  // A type to store the type of the response
  String type = "";
  // A message to store the message of the response
  String message = "";

  // Constructor to initialize the type and message of the response
  Response({required this.type, required this.message});

  // A factory constructor to initialize the response object from a json map
  Response.fromJson(Map<String, dynamic> json) {
    type = json['type'];
    message = json['message'];
  }

  // A method to convert the response object to a json map
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['type'] = type;
    data['message'] = message;
    return data;
  }
}
