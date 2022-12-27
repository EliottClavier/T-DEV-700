package com.api.gateway.data;

public class DestinationGenerator {

    public static final String MESSAGE_PREFIX_SHOP = "/queue/shop";
    public static final String MESSAGE_PREFIX_TPE = "/queue/tpe";

    public static final String MESSAGE_PREFIX_SERVER = "/queue/server";

    /* Server messages */
    // Used to send a global message (every web socket connections)
    public String getServerStatusDest() {
        return MESSAGE_PREFIX_SERVER + "/server-status/";
    }

    /* Shop */
    public String getShopTransactionStatusDest(String sessionId) {
        return String.format("%s/transaction-status/%s", MESSAGE_PREFIX_SHOP, sessionId);
    }

    /* Tpe */
    public String getTpeSynchronizationStatusDest(String sessionId) {
        return String.format("%s/synchronization-status/%s", MESSAGE_PREFIX_TPE, sessionId);
    }

    public String getTpeTransactionStatusDest(String sessionId) {
        return String.format("%s/transaction-status/%s", MESSAGE_PREFIX_TPE, sessionId);
    }
}
