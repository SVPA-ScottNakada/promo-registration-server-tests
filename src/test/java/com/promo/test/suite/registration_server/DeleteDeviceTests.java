
package com.promo.test.suite.registration_server;

import com.promo.test.framework.registration_server.DeleteDeviceHelper;
import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.BaseApiTest;

import org.testng.annotations.Test;

public class DeleteDeviceTests extends BaseApiTest {

    public static final String TEST_DUID = RegistrationServerTestData.DUID;
    
    public static final String TEST_DUID_SECOND = RegistrationServerTestData.DUID_02;

    public static final String TEST_APP = RegistrationServerTestData.APP_NAME;

    public static final String TEST_APP_KEY = RegistrationServerTestData.APP_KEY;

    public static final String TEST_EMAIL = RegistrationServerTestData.EMAIL;

    @TestData(description = "Delete test Device")
    @Test(groups = "SmokeTest")
    public void deleteDeviceTest() {

        DeleteDeviceHelper delDev = new DeleteDeviceHelper();
        delDev.addDeviceUserId(TEST_DUID);
        delDev.send();

        delDev.validateResponseCodeOk();

    }
    
    @TestData(description = "Delete second test Device")
    @Test(groups = "SmokeTest")
    public void deleteSecondDeviceTest() {

        DeleteDeviceHelper delDev = new DeleteDeviceHelper();
        delDev.addDeviceUserId(TEST_DUID_SECOND);
        delDev.send();

        delDev.validateResponseCodeOk();

    }

}
