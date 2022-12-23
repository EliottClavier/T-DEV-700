class Token {
  String token = "";

  Token({required this.token});

  Token.fromJson(Map<String, dynamic> json) {
    if (json['token'].toString().isEmpty) {
      throw Exception(json["message"]);
    } else {
      token = json['token'];
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['token'] = token;
    return data;
  }
}
