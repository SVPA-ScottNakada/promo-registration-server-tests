
package com.promo.test.framework.registration_server;

import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RedeemHelper extends RegistrationServerApiCallHelper {

    private static final String API_COMMAND = "redeem";

    public RedeemHelper() {
        this(RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);
    }

    public RedeemHelper(String newUri) {
        super(newUri + API_COMMAND);
    }

    // Send request

    public void send() {
        sendPostRequest();
    }

    // Add Parameters

    public void addApplicationId(String value) {
        addStringAsBodyParameter("appId", value);
    }

    public void addApplicationVersion(String value) {
        addStringAsBodyParameter("appVersion", value);
    }

    public void addDeviceUserId(String value) {
        addStringAsBodyParameter("duid", value);
    }

    public void addLanguage(String value) {
        addStringAsBodyParameter("lang", value);
    }

    public void addModel(String value) {
        addStringAsBodyParameter("model", value);
    }

    public void addEmail(String value) {
        addStringAsBodyParameter("email", value);
    }

    public void addPromoMeta(String value) {
        addStringAsBodyParameter("promoMeta", value);
    }

}
