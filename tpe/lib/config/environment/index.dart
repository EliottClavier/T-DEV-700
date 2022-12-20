// ignore_for_file: constant_identifier_names
import 'package:tpe/config/environment/local/config.dart';
import 'package:tpe/config/environment/prod/config.dart';

const String ENV = String.fromEnvironment('ENV', defaultValue: 'local');

const String API_URL = ENV == 'local' ? LOCAL_API_URL : PROD_API_URL;
const String REGISTER_HEADER =
    ENV == 'local' ? LOCAL_REGISTER_HEADER : PROD_REGISTER_HEADER;
const String REGISTER_KEY =
    ENV == 'local' ? LOCAL_REGISTER_KEY : PROD_REGISTER_KEY;
