// ignore_for_file: constant_identifier_names
const String PROD_HTTP_PROTOCOL = "https";
const String PROD_API_URL =
    String.fromEnvironment('API_URL', defaultValue: 'api.cash-manager.live');
const String PROD_REGISTER_HEADER = String.fromEnvironment(
    'TPE_REGISTER_SECRET_HEADER',
    defaultValue: 'secretHeader');
const String PROD_REGISTER_KEY = String.fromEnvironment(
    'TPE_REGISTER_SECRET_KEY',
    defaultValue: 'secretKey');
