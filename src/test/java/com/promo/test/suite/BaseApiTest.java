
package com.promo.test.suite;

import com.promo.test.suite.BaseTest;

import org.testng.annotations.BeforeClass;

public class BaseApiTest extends BaseTest {

    @BeforeClass
    public static void setUpBeforeClass() {

        BaseTest.setUpBeforeClass();

    }

}
