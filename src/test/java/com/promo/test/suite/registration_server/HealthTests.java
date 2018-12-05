
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.HealthHelper;

import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class HealthTests extends BaseApiTest {

    @Test(groups = "BrokenTest")
    public void healthTest() {

        HealthHelper health = new HealthHelper();
        health.send();

        health.validateResponseCodeOk();

    }

}
