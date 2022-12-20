// ignore_for_file: constant_identifier_names

const String LOCAL_API_URL = "192.168.1.11:8080/api";
const String LOCAL_REGISTER_HEADER = String.fromEnvironment(
    'TPE_REGISTER_SECRET_HEADER',
    defaultValue: 'secretHeader');
const String LOCAL_REGISTER_KEY = String.fromEnvironment(
    'TPE_REGISTER_SECRET_KEY',
    defaultValue: 'secretKey');
