
package com.promo.test.framework.registration_server;

import com.promo.test.framework.utils.TestRailRequestHelper;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

public class RegistrationServerTestRailHelper extends TestRailRequestHelper {

    public RegistrationServerTestRailHelper() {
        super("Registration Server", RegistrationServerTestData.TESTRAIL_PROMO_PROJECT_ID,
                RegistrationServerTestData.TESTRAIL_SUITE_REGISTRATION_SERVER_ID);

        if (!getRunDescription().isEmpty()) {
            return;
        }

        appendToRunDescription("Base Uri: " + RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI);

    }
}
