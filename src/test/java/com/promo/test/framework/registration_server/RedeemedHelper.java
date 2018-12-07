
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RedeemedHelper extends RegistrationServerRequestHelper {

    private static final String API_COMMAND = "redeemed";

    public RedeemedHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RedeemedHelper(String newUri) {
        super(newUri + API_COMMAND);
    }

    // Send request

    public void send() {
        sendGetRequest();
    }

    // Add Parameters

    public void addApplicationId(String value) {
        addStringAsRequestParameter("appId", value);
    }

    public void addApplicationVersion(String value) {
        addStringAsRequestParameter("appVersion", value);
    }

    public void addDeviceUserId(String value) {
        addStringAsRequestParameter("duid", value);
    }

    public void addDeviceToken(String value) {
        addStringAsRequestParameter("deviceToken", value);
    }

    // @formatter:off
    // --- JSON RESPONSE PATHS --- ////

    public static final String PROMOS = "promos";
    public static final String PROMO_ID = PROMOS + ".promoId";
    public static final String IS_REDEEMED_BY_USER = PROMOS + ".isRedeemedByUser";
    public static final String REDEEM_DATE = PROMOS + ".redeemDate";

    // @formatter:on
}
