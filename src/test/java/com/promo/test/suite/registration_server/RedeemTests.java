
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RedeemHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class RedeemTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_PROMOMETA =
            "https://api.erabu.sony.tv/f1bdee86d512265979ab627fcf2133235f6f9817/8ec3f875-c05b-43cb-bc5e-d66c8fc50d8c/items/6692";

    @TestData(id = "", description = "Required parameters")
    @Test(groups = "BrokenTest")
    public void requiredParametersTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

    @TestData(id = "", description = "Missing appId parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member appId");

    }

    @TestData(id = "", description = "Missing appVersion parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member appVersion");

    }

    @TestData(id = "", description = "Missing duid parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member duid");

    }

    @TestData(id = "", description = "Missing lang parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingLangTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member lang");

    }

    @TestData(id = "", description = "Missing model parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingModelTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member model");

    }

    @TestData(id = "", description = "Missing email parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingEmailTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member email");

    }

    @TestData(id = "", description = "Missing promoMeta parameter")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingPromoMetaTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4005");
        redeem.validateDebug("4005", "Missing member promoMeta");

    }

    @TestData(id = "", description = "No app key, invalid signature")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeem.validateDebug("4001", "Invalid signature");

    }

    @TestData(id = "", description = "Invalid appId")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppIdTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId("ThisShouldNotWork");
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeem.validateDebug("4001", "Missing App key");

    }

    // TODO: Finish validations
    @TestData(id = "", description = "Invalid duid")
    @Test(groups = {"BrokenTest", "NegativeTest"})
    public void invalidDuidTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId("ThisShouldNotWork");
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

    @TestData(id = "", description = "Invalid language pattern")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidLanguagePatternTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("eng");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        redeem.validateDebug("4002", "Invalid pattern for lang.");

    }

    // TODO: Finish validations
    @TestData(id = "", description = "Invalid model")
    @Test(groups = {"BrokenTest", "NegativeTest"})
    public void invalidModelTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("ThisShouldNotWork");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

    @TestData(id = "", description = "Invalid email pattern")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidEmailPatternTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail("ThisShouldNotWork");
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        redeem.validateDebug("4002", "Invalid pattern for email.");

    }

    @TestData(id = "", description = "Invalid promoMeta pattern")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidPromoMetaPatternTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta("ThisShouldNotWork");
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        redeem.validateDebug("4002", "Invalid pattern for promoMeta.");

    }

    @TestData(id = "", description = "Invalid promoMeta")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidPromoMetaTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta("https://api.erabu.sony.tv/ThisShouldNotWork/ThisWontWork/1234");
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4301");

    }

    @TestData(id = "", description = "Invalid app key")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppKeyTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey("ThisShouldNotWork");
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeem.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeem.validateDebug("4001", "Invalid signature");

    }
}
