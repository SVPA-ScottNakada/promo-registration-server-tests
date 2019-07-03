
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.DeleteDeviceHelper;
import com.promo.test.framework.registration_server.RedeemHelper;
import com.promo.test.framework.registration_server.RedeemedHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class MultiplePromoFlowTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID_FLOW;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_REGMETA = RegistrationServerTestData.REGMETA;

    public static String deviceToken = "";

    public static final String TEST_PROMOMETA_URL_01 = RegistrationServerTestData.PROMOMETA_URL_001;

    public static final String TEST_PROMOMETA_NAME_01 = RegistrationServerTestData.PROMOMETA_NAME_001;

    public static final String TEST_PROMOMETA_URL_02 = RegistrationServerTestData.PROMOMETA_URL_002;

    public static final String TEST_PROMOMETA_NAME_02 = RegistrationServerTestData.PROMOMETA_NAME_002;

    public static final String TEST_PROMOMETA_URL_03 = RegistrationServerTestData.PROMOMETA_URL_003;

    public static final String TEST_PROMOMETA_NAME_03 = RegistrationServerTestData.PROMOMETA_NAME_003;

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     */
    @TestData(id = "1526450", description = "Register device, expected status code = 200")
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

    /**
     * --Preconditions
     * Run test case id = "1526450", description = "Register device"
     * --Steps
     * send register/email post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * email = TEST_EMAIL
     * optIn = true
     * registerMeta = TEST_REGMETA
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     * deviceToken not null or empty
     * get test deviceToken
     */
    @TestData(id = "1526451", description = "Register email to device, expected status code = 200")
    @Test(groups = "SmokeTest", dependsOnMethods = {"registerDeviceTest"}, alwaysRun = true)
    public void registerEmailTest() {

        RegisterEmailHelper regEmail = new RegisterEmailHelper();
        regEmail.addApplicationId(TEST_APP);
        regEmail.addApplicationVersion("0.1");
        regEmail.addDeviceUserId(TEST_DUID);
        regEmail.addEmail(TEST_EMAIL);
        regEmail.addOptIn(true);
        regEmail.addRegisterMeta(TEST_REGMETA);
        regEmail.setAppKey(TEST_APP_KEY);
        regEmail.send();

        regEmail.validateResponseCodeOk();
        regEmail.validateNotNullOrEmpty(RegisterEmailHelper.DEVICE_TOKEN);

        regEmail.log("Saving value for the first deviceToken");
        deviceToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);

    }

    /**
     * --Preconditions
     * Run test case id = "1526451", description = "Register email to device"
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     * promos not null or empty
     * promos list count = 0
     */
    @TestData(id = "1526452", description = "Check empty promo list, expected promos list count = 0")
    @Test(groups = "SmokeTest", dependsOnMethods = {"registerEmailTest"}, alwaysRun = true)
    public void emptyRedeemedTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(deviceToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validateNotNullOrEmpty(RedeemedHelper.PROMOS);
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 0);

    }

    /**
     * --Preconditions
     * Run test case id = "1526452", description = "Check empty promo list"
     * --Steps
     * send redeem post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * email = TEST_EMAIL
     * promoMeta = TEST_PROMOMETA_01
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     */
    @TestData(id = "1526453", description = "Redeem first promo, expected status code = 200")
    @Test(groups = "SmokeTest", dependsOnMethods = {"emptyRedeemedTest"}, alwaysRun = true)
    public void reedemFirstPromoTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA_URL_01);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

    /**
     * --Preconditions
     * Run test case id = "1526453", description = "Redeem first promo"
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     * promos list count = 1
     * promoId list contains TEST_PROMOMETA_ID_01
     * isRedeemedByUser not null or empty
     * redeemDate not null or empty
     */
    @TestData(id = "1526454", description = "Check promo list for first redeemed promo, expected promos count = 1")
    @Test(groups = "SmokeTest", dependsOnMethods = {"reedemFirstPromoTest"}, alwaysRun = true)
    public void validateFirstRedeemedTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(deviceToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 1);
        redeemed.validateValueInList(RedeemedHelper.PROMO_ID, TEST_PROMOMETA_NAME_01);
        redeemed.validateValueInList(RedeemedHelper.IS_REDEEMED_BY_USER, true);
        redeemed.validateNotNullOrEmpty(RedeemedHelper.REDEEM_DATE);

    }

    /**
     * --Preconditions
     * Run test case id = "1526454", description = "Check promo list for first redeemed promo"
     * --Steps
     * send redeem post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * email = TEST_EMAIL
     * promoMeta = TEST_PROMOMETA_02
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     */
    @TestData(id = "1526455", description = "Redeem second promo, expected status code = 200")
    @Test(groups = "SmokeTest", dependsOnMethods = {"validateFirstRedeemedTest"}, alwaysRun = true)
    public void reedemSecondPromoTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA_URL_02);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

    /**
     * Preconditions
     * Run test case id = "1526455", description = "Reedem second promo"
     * Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * Expected Result
     * http status code = 200
     * promos list count = 2
     * promoId list contains TEST_PROMOMETA_ID_02
     */
    @TestData(id = "1526456", description = "Check promo list for second redeemed promo, expected promos count = 2")
    @Test(groups = "SmokeTest", dependsOnMethods = {"reedemSecondPromoTest"}, alwaysRun = true)
    public void validateSecondRedeemedTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(deviceToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 2);
        redeemed.validateValueInList(RedeemedHelper.PROMO_ID, TEST_PROMOMETA_NAME_02);

    }

    /**
     * --Preconditions
     * Run test case id = "1526456", description = "Check promo list for second redeemed promo"
     * --Steps
     * send redeem post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * email = TEST_EMAIL
     * promoMeta = TEST_PROMOMETA_03
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     */
    @TestData(id = "1526457", description = "Redeem third promo, expected status code = 200")
    @Test(groups = "SmokeTest", dependsOnMethods = {"validateSecondRedeemedTest"}, alwaysRun = true)
    public void reedemThirdPromoTest() {

        RedeemHelper redeem = new RedeemHelper();
        redeem.addApplicationId(TEST_APP);
        redeem.addApplicationVersion("0.1");
        redeem.addDeviceUserId(TEST_DUID);
        redeem.addLanguage("en");
        redeem.addModel("some-tv");
        redeem.addEmail(TEST_EMAIL);
        redeem.addPromoMeta(TEST_PROMOMETA_URL_03);
        redeem.setAppKey(TEST_APP_KEY);
        redeem.send();

        redeem.validateResponseCodeOk();

    }

    /**
     * --Preconditions
     * Run test case id = "1526457", description = "Redeem third promo"
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     * promos list count = 3
     * promoId list contains TEST_PROMOMETA_ID_03
     */
    @TestData(id = "1526458", description = "Check promo list for third redeemed promo, expected promos count = 3")
    @Test(groups = "SmokeTest", dependsOnMethods = {"reedemThirdPromoTest"}, alwaysRun = true)
    public void validateThirdRedeemedTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(deviceToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 3);
        redeemed.validateValueInList(RedeemedHelper.PROMO_ID, TEST_PROMOMETA_NAME_03);

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * Run test case id = "1526458", description = "Check promo list for third redeemed promo"
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = "x10101111xx11fzz1b1101101d11cdf01c101gx1d001101f1fb111c0c111d1z1"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     * promos not null or empty
     * promos list count = 0
     */
    @TestData(id = "1526338", description = "redeemed - Invalid deviceToken, expected promos list count = 0")
    @Test(groups = {"SmokeTest", "NegativeTest", "DeprecatedTest"}, dependsOnMethods = {"validateThirdRedeemedTest"},
            alwaysRun = true)
    public void invalidDeviceTokenTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken("x10101111xx11fzz1b1101101d11cdf01c101gx1d001101f1fb111c0c111d1z1");
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validateNotNullOrEmpty(RedeemedHelper.PROMOS);
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 0);

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * Run test case id = "1526458", description = "Check promo list for third redeemed promo"
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = "ThisShouldNotWork"
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 200
     * promos not null or empty
     * promos list count = 0
     */
    @TestData(id = "1526740", description = "redeemed - Invalid duid, expected promos count = 0")
    @Test(groups = {"SmokeTest", "NegativeTest"}, dependsOnMethods = {"validateThirdRedeemedTest"}, alwaysRun = true)
    public void invalidDuidTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId("ThisShouldNotWork");
        redeemed.addDeviceToken(deviceToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validateNotNullOrEmpty(RedeemedHelper.PROMOS);
        redeemed.validatePathCount(RedeemedHelper.PROMOS, 0);

    }

    @TestData(description = "Delete Flow device")
    @Test(groups = "SmokeTest", dependsOnMethods = {"invalidDuidTest"}, alwaysRun = true)
    public void deleteFlowDeviceTest() {

        DeleteDeviceHelper delDev = new DeleteDeviceHelper();
        delDev.addDeviceUserId(TEST_DUID);
        delDev.send();

        delDev.validateResponseCodeOk();

    }

}
