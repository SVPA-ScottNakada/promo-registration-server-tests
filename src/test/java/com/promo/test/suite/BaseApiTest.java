
package com.promo.test.suite;

import com.promo.test.suite.BaseTest;

import org.testng.annotations.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;

public class BaseApiTest extends BaseTest {

    @BeforeClass
    public static void setUpBeforeClass() {

        BaseTest.setUpBeforeClass();

        setUpJasonParser();
        RestAssured.useRelaxedHTTPSValidation();
    }

    public static void setUpJasonParser() {
        RestAssured.defaultParser = Parser.JSON;
    }

}
