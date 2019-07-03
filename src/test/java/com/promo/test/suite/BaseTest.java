
package com.promo.test.suite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    static {
        System.setProperty("log4j.configurationFile", "var/secure/Log4j.properties");
    }

    protected Logger log = LogManager.getLogger(getClass());

    @BeforeClass
    public static void setUpBeforeClass() {

    }

}
