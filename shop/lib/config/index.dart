
const String ENV = String.fromEnvironment('ENV', defaultValue: 'prod');

const String HTTP_PROTOCOL = ENV == "local" ? "http" : "https";

const String API_URL =
    String.fromEnvironment('API_URL', defaultValue: 'api.cash-manager.live');

const String SHOP_USERNAME =
    String.fromEnvironment('SHOP_USERNAME', defaultValue: 'SHOP');
const String SHOP_PASSWORD =
    String.fromEnvironment('SHOP_PASSWORD', defaultValue: 'PASSWORD');
