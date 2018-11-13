
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.DeleteDeviceHelper;
import com.promo.test.framework.registration_server.HealthHelper;
import com.promo.test.framework.registration_server.RedeemHelper;
import com.promo.test.framework.registration_server.RedeemedHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class OneOfEachTests extends BaseApiTest {

    public static final String TEST_DUID = "abcdefghi_mytest";

    public static final String TEST_APP = "postman";

    public static final String TEST_APP_KEY = "hogehoge";

    public static final String TEST_EMAIL = "this@email.com";

    @Test(groups = "SmokeTest")
    public void registerDeviceTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCodeOk();

    }

    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void registerDeviceTestMissingAppId() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(422);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member appId");

    }

    @Test(groups = "SmokeTest")
    public void deleteDeviceTest() {

        DeleteDeviceHelper delDev = new DeleteDeviceHelper();
        delDev.addDeviceUserId(TEST_DUID);
        delDev.send();

        delDev.validateResponseCodeOk();

    }

    @Test(groups = "SmokeTest")
    public void healthTest() {

        HealthHelper health = new HealthHelper();
        health.send();

        health.validateResponseCodeOk();

    }

    @Test(groups = "BrokenTest")
    public void registerEmailTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(
                "https://api.erabu.sony.tv/f1bdee86d512265979ab627fcf2133235f6f9817/8ec3f875-c05b-43cb-bc5e-d66c8fc50d8c/items/6691");
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCodeOk();

    }

    @Test(groups = "BrokenTest")
    public void redeemedTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken("abc123");
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();

    }

    @Test(groups = "BrokenTest")
    public void redeemTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(
                "https://api.erabu.sony.tv/f1bdee86d512265979ab627fcf2133235f6f9817/8ec3f875-c05b-43cb-bc5e-d66c8fc50d8c/items/6692");
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

}
