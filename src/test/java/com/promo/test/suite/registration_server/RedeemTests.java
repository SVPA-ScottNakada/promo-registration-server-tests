
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RedeemHelper;
import com.promo.test.framework.registration_server.RedeemedHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RedeemTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;

    public static final String TEST_DUID_SECOND = RegistrationServerTestData.DUID_02;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_SECOND_EMAIL = RegistrationServerTestData.SECOND_EMAIL;

    public static final String TEST_REGMETA = RegistrationServerTestData.REGMETA;

    public static final String TEST_PROMOMETA = RegistrationServerTestData.PROMOMETA_001;

    public static final String TEST_PROMOMETA_ID = RegistrationServerTestData.PROMOMETA_ID_001;

    @BeforeClass(groups = "SmokeTest")
    public void registerDeviceAndEmail() {
        // make Sure Device Is Registered
        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.logToReport("Make sure Device is registered");
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();
        regDev.validateResponseCodeOk();

        // make Sure Second Device Is Registered
        RegisterDeviceHelper regDev02 = new RegisterDeviceHelper();
        regDev02.logToReport("Make sure second Device is registered");
        regDev02.addApplicationId(TEST_APP);
        regDev02.addApplicationVersion("0.1");
        regDev02.addDeviceUserId(TEST_DUID_SECOND);
        regDev02.addLanguage("en");
        regDev02.addModel("some-tv");
        regDev02.setAppKey(TEST_APP_KEY);
        regDev02.send();
        regDev02.validateResponseCodeOk();

        // make Sure Email Is Registered
        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.logToReport("Make sure Email is registered");
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();
        regEmail.validateResponseCodeOk();

        // make Sure Email Is Registered to Second Device
        RegisterEmailHelper regEmail2ndDev = new RegisterEmailHelper();
        regEmail2ndDev.logToReport("Make sure Email is registered to second device");
        regEmail2ndDev.addApplicationId(TEST_APP);
        regEmail2ndDev.addApplicationVersion("0.1");
        regEmail2ndDev.addDeviceUserId(TEST_DUID_SECOND);
        regEmail2ndDev.addEmail(TEST_EMAIL);
        regEmail2ndDev.addOptIn(true);
        regEmail2ndDev.addRegisterMeta(TEST_REGMETA);
        regEmail2ndDev.setAppKey(TEST_APP_KEY);
        regEmail2ndDev.send();
        regEmail2ndDev.validateResponseCodeOk();

        // make Sure Second Email Is Registered to First Device
        RegisterEmailHelper reg2ndEmail = new RegisterEmailHelper();
        reg2ndEmail.addApplicationId(TEST_APP);
        reg2ndEmail.addApplicationVersion("0.1");
        reg2ndEmail.addDeviceUserId(TEST_DUID);
        reg2ndEmail.addEmail(TEST_SECOND_EMAIL);
        reg2ndEmail.addOptIn(true);
        reg2ndEmail.addRegisterMeta(TEST_REGMETA);
        reg2ndEmail.setAppKey(TEST_APP_KEY);
        reg2ndEmail.send();
        reg2ndEmail.validateResponseCodeOk();

    }

    @TestData(id = "1526340", description = "Required parameters")
    @Test(groups = "SmokeTest")
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

    @TestData(id = "1526735", description = "Redeem same promo and email for Second duid")
    @Test(groups = "SmokeTest", dependsOnMethods = {"requiredParametersTest"}, alwaysRun = true)
    public void samePromoAndEmailForSecondDuidTest() {

        RedeemHelper redeemOn2ndDevice = new RedeemHelper();
        redeemOn2ndDevice.addApplicationId(TEST_APP);
        redeemOn2ndDevice.addApplicationVersion("0.1");
        redeemOn2ndDevice.addDeviceUserId(TEST_DUID_SECOND);
        redeemOn2ndDevice.addLanguage("en");
        redeemOn2ndDevice.addModel("some-tv");
        redeemOn2ndDevice.addEmail(TEST_EMAIL);
        redeemOn2ndDevice.addPromoMeta(TEST_PROMOMETA);
        redeemOn2ndDevice.setAppKey(TEST_APP_KEY);
        redeemOn2ndDevice.send();

        redeemOn2ndDevice.validateResponseCodeOk();

    }

    @TestData(id = "1526736", description = "Cant Redeem same promo and duid for Second Email")
    @Test(groups = {"SmokeTest", "NegativeTest"}, dependsOnMethods = {"requiredParametersTest"}, alwaysRun = true)
    public void samePromoAndDuidForSecondEmailTest() {

        RedeemHelper redeemWith2ndEmail = new RedeemHelper();
        redeemWith2ndEmail.addApplicationId(TEST_APP);
        redeemWith2ndEmail.addApplicationVersion("0.1");
        redeemWith2ndEmail.addDeviceUserId(TEST_DUID);
        redeemWith2ndEmail.addLanguage("en");
        redeemWith2ndEmail.addModel("some-tv");
        redeemWith2ndEmail.addEmail(TEST_SECOND_EMAIL);
        redeemWith2ndEmail.addPromoMeta(TEST_PROMOMETA);
        redeemWith2ndEmail.setAppKey(TEST_APP_KEY);
        redeemWith2ndEmail.send();

        redeemWith2ndEmail.validateResponseCode(HttpStatus.SC_BAD_REQUEST);
        redeemWith2ndEmail.validateValue(RedeemHelper.ERROR_CODE_PATH, "4501");
        redeemWith2ndEmail.validateDebug("4501", "Promo redeemed already. PromoId: '" + TEST_PROMOMETA_ID + "'");

    }

    @TestData(id = "1526447", description = "Validate redeem promo shows up in the redeemed call")
    @Test(groups = "SmokeTest", dependsOnMethods = {"requiredParametersTest"}, alwaysRun = true)
    public void validateRedeemTest() {
        // We get the deviceToken
        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        String testDevToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);

        // We validate that the promo appears
        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 1);
        redeemed.validateValueInList(RedeemedHelper.PROMO_ID, TEST_PROMOMETA_ID);
        redeemed.validateValueInList(RedeemedHelper.IS_REDEEMED_BY_USER, true);
        redeemed.validateNotNullOrEmpty(RedeemedHelper.REDEEM_DATE);

    }

    @TestData(id = "1526737", description = "Validate redeem promo also shows up for Second email in the redeemed call")
    @Test(groups = "SmokeTest", dependsOnMethods = {"samePromoAndDuidForSecondEmailTest", "validateRedeemTest"},
            alwaysRun = true)
    public void Test() {
        // We get the deviceToken
        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_SECOND_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        String testDevToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);

        // We validate that the promo appears
        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 1);
        redeemed.validateValueInList(RedeemedHelper.PROMO_ID, TEST_PROMOMETA_ID);
        redeemed.validateValueInList(RedeemedHelper.IS_REDEEMED_BY_USER, false);
        redeemed.validateNotNullOrEmpty(RedeemedHelper.REDEEM_DATE);

    }

    @TestData(id = "1526448", description = "Promo redeemed already")
    @Test(groups = {"SmokeTest", "NegativeTest"}, dependsOnMethods = {"requiredParametersTest"}, alwaysRun = true)
    public void promoAlreadyRedeemedTest() {

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

        redeem.validateResponseCode(HttpStatus.SC_BAD_REQUEST);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4501");
        redeem.validateDebug("4501", "Promo redeemed already. PromoId: '" + TEST_PROMOMETA_ID + "'");

    }

    @TestData(id = "1526732", description = "Redeem with Unregistered email")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void unregisteredEmailTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail("thiswontwork_182@outlook.com");
        redeem.addPromoMeta(TEST_PROMOMETA);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCode(HttpStatus.SC_BAD_REQUEST);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4202");
        redeem.validateDebug("4202", "Device email not registered");

    }

    @TestData(id = "1526341", description = "Missing appId parameter")
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

    @TestData(id = "1526342", description = "Missing appVersion parameter")
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

    @TestData(id = "1526343", description = "Missing duid parameter")
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

    @TestData(id = "1526344", description = "Missing lang parameter")
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

    @TestData(id = "1526345", description = "Missing model parameter")
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

    @TestData(id = "1526346", description = "Missing email parameter")
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

    @TestData(id = "1526347", description = "Missing promoMeta parameter")
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

    @TestData(id = "1526348", description = "No app key, invalid signature")
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

    @TestData(id = "1526349", description = "Invalid appId")
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

    @TestData(id = "1526350", description = "Invalid duid")
    @Test(groups = {"SmokeTest", "NegativeTest"})
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

        redeem.validateResponseCode(HttpStatus.SC_BAD_REQUEST);
        redeem.validateValue(RedeemHelper.ERROR_CODE_PATH, "4202");
        redeem.validateDebug("4202", "Device email not registered");

    }

    @TestData(id = "1526351", description = "Invalid language pattern")
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

    @TestData(id = "1526353", description = "Invalid email pattern")
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

    @TestData(id = "1526354", description = "Invalid promoMeta pattern")
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

    @TestData(id = "1526355", description = "Invalid promoMeta")
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

    @TestData(id = "1526356", description = "Invalid app key")
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
