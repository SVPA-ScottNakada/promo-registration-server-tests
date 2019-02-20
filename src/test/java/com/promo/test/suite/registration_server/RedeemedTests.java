
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RedeemedHelper;
import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.registration_server.RegisterEmailHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RedeemedTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    public static final String TEST_REGMETA = RegistrationServerTestData.REGMETA;

    public static String testDevToken = "";

    @BeforeClass(groups = "SmokeTest")
    public void setDevToken() {
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

        // get devToken
        testDevToken = regEmail.getPathValue(RegisterEmailHelper.DEVICE_TOKEN);

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
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
     */
    @TestData(id = "1526330", description = "Required parameters, expected status code = 200")
    @Test(groups = "SmokeTest")
    public void requiredParametersTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCodeOk();
        redeemed.validateNotNullOrEmpty(RedeemedHelper.PROMOS);

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member appId"
     */
    @TestData(id = "1526331", description = "Missing appId parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member appId");

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member appVersion"
     */
    @TestData(id = "1526332", description = "Missing appVersion parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member appVersion");

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member duid"
     */
    @TestData(id = "1526333", description = "Missing duid parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member duid");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member deviceToken"
     */
    @TestData(id = "1526334", description = "Missing deviceToken parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDeviceTokenTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RedeemedHelper.ERROR_CODE_PATH, "4005");
        redeemed.validateDebug("4005", "Missing member deviceToken");

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature Without the appKey
     * --Expected Result
     * http status code = 401
     * error code = "4001"
     * debug code = "4001"
     * debug message = "Invalid signature"
     */
    @TestData(id = "1526335", description = "No app key, invalid signature, expected error code = 4001")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Invalid signature");

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = "ThisShouldNotWork"
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using the appKey
     * --Expected Result
     * http status code = 401
     * error code = "4001"
     * debug code = "4001"
     * debug message = "Missing App key"
     */
    @TestData(id = "1526336", description = "Invalid appId, expected error code = 4001")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppIdTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId("ThisShouldNotWork");
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Missing App key");

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = "ThisShouldNotWork"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4002"
     * debug code = "4002"
     * debug message = "Invalid pattern for deviceToken."
     */
    @TestData(id = "1526337", description = "Invalid deviceToken pattern, expected error code = 4002")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidDeviceTokenPatternTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken("ThisShouldNotWork");
        redeemed.setAppKey(TEST_APP_KEY);
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        redeemed.validateDebug("4002", "Invalid pattern for deviceToken.");

    }

    /**
     * --Preconditions
     * Have a valid test deviceToken.
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send redeemed get request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * deviceToken = TEST_DEV_TOKEN
     * generate signature using "ThisShouldNotWork" as the appKey
     * --Expected Result
     * http status code = 401
     * error code = "4001"
     * debug code = "4001"
     * debug message = "Invalid signature"
     */
    @TestData(id = "1526339", description = "Invalid app key, expected error code = 4001")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppKeyTest() {

        RedeemedHelper redeemed = new RedeemedHelper();
        redeemed.addApplicationId(TEST_APP);
        redeemed.addApplicationVersion("0.1");
        redeemed.addDeviceUserId(TEST_DUID);
        redeemed.addDeviceToken(testDevToken);
        redeemed.setAppKey("ThisShouldNotWork");
        redeemed.send();

        redeemed.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        redeemed.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        redeemed.validateDebug("4001", "Invalid signature");

    }

}
