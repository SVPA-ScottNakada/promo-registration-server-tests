
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.RegisterDeviceHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class RegisterDeviceTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

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
    @TestData(id = "1526320", description = "Required parameters, expected status code = 200")
    @Test(groups = "SmokeTest")
    public void requiredParametersTest() {

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
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member appId"
     */
    @TestData(id = "1526321", description = "Missing appId parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppIdTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member appId");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = TEST_APP
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member appVersion"
     */
    @TestData(id = "1526322", description = "Missing appVersion parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingAppVersionTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member appVersion");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * lang = "en"
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member duid"
     */
    @TestData(id = "1526323", description = "Missing duid parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingDuidTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member duid");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member lang"
     */
    @TestData(id = "1526324", description = "Missing lang parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingLangTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member lang");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4005"
     * debug code = "4005"
     * debug message = "Missing member model"
     */
    @TestData(id = "1526325", description = "Missing model parameter, expected error code = 4005")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void missingModelTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4005");
        regDev.validateDebug("4005", "Missing member model");

    }

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
     * generate signature Without the appKey
     * --Expected Result
     * http status code = 401
     * error code = "4001"
     * debug code = "4001"
     * debug message = "Invalid signature"
     */
    @TestData(id = "1526326", description = "No app key, invalid signature, expected error code = 4001")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidSignatureTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regDev.validateDebug("4001", "Invalid signature");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = "ThisShouldNotWork"
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "en"
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 401
     * error code = "4001"
     * debug code = "4001"
     * debug message = "Missing App key"
     */
    @TestData(id = "1526327", description = "Invalid appId, expected error code = 4001")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppIdTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId("ThisShouldNotWork");
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regDev.validateDebug("4001", "Missing App key");

    }

    /**
     * --Preconditions
     * Have a valid test appId and appKey to generate signature.
     * --Steps
     * send register/device post request using:
     * appId = TEST_APP
     * appVersion = "0.1"
     * duid = TEST_DUID
     * lang = "eng"
     * model = "some-tv"
     * generate signature using the appKey
     * --Expected Result
     * http status code = 422
     * error code = "4002"
     * debug code = "4002"
     * debug message = "Invalid pattern for lang."
     */
    @TestData(id = "1526328", description = "Invalid language pattern, expected error code = 4002")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidLanguagePatternTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("eng");
        regDev.addModel("some-tv");
        regDev.setAppKey(TEST_APP_KEY);
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4002");
        regDev.validateDebug("4002", "Invalid pattern for lang.");

    }

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
     * generate signature using "ThisShouldNotWork" as the appKey
     * --Expected Result
     * http status code = 401
     * error code = "4001"
     * debug code = "4001"
     * debug message = "Invalid signature"
     */
    @TestData(id = "1526329", description = "Invalid app key, expected error code = 4001")
    @Test(groups = {"SmokeTest", "NegativeTest"})
    public void invalidAppKeyTest() {

        RegisterDeviceHelper regDev = new RegisterDeviceHelper();
        regDev.addApplicationId(TEST_APP);
        regDev.addApplicationVersion("0.1");
        regDev.addDeviceUserId(TEST_DUID);
        regDev.addLanguage("en");
        regDev.addModel("some-tv");
        regDev.setAppKey("ThisShouldNotWork");
        regDev.send();

        regDev.validateResponseCode(HttpStatus.SC_UNAUTHORIZED);
        regDev.validateValue(RegisterDeviceHelper.ERROR_CODE_PATH, "4001");
        regDev.validateDebug("4001", "Invalid signature");

    }

}
