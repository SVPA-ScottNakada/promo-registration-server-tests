
package com.promo.test.framework.registration_server;

import static io.restassured.RestAssured.given;

import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.CommonTestData;
import com.promo.test.suite.registration_server.RegistrationServerTestData;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TestRailRequestHelper implements ITestListener, ISuiteListener, IInvokedMethodListener {

    static {
        System.setProperty("log4j.configurationFile", "var/secure/Log4j.properties");
    }

    protected Logger log = LogManager.getLogger(getClass());

    protected Response requestResponse = null;

    private static final String ACTIVE = CommonTestData.TESTRAIL_ACTIVE;

    private static final String USER = CommonTestData.TESTRAIL_USER;

    private static final String USER_ID = CommonTestData.TESTRAIL_USER_ID;

    private static final String PASSWORD = CommonTestData.TESTRAIL_PASSWORD;

    private static final String API_URL = CommonTestData.TESTRAIL_API_URL;

    private static final String PROJECT_ID = CommonTestData.TESTRAIL_PROJECT_ID;

    private static final String SUITE_ID = CommonTestData.TESTRAIL_SUITE_REGISTRATION_SERVER_ID;

    private static String testRailRunName = null;

    private static String testRailRunCreationTime = null;

    private static String testRailRunId = null;

    public static final String TEST_RAIL_RESULT_PASSED = "1";

    public static final String TEST_RAIL_RESULT_BLOCKED = "2";

    public static final String TEST_RAIL_RESULT_UNTESTED = "3";

    public static final String TEST_RAIL_RESULT_RETEST = "4";

    public static final String TEST_RAIL_RESULT_FAILED = "5";

    public TestRailRequestHelper() {
        RestAssured.useRelaxedHTTPSValidation();
    }

    // This belongs to ISuiteListener and will execute before the Suite start
    @Override
    public void onStart(ISuite arg0) {

    }

    // This belongs to ISuiteListener and will execute, once the Suite is finished
    @Override
    public void onFinish(ISuite arg0) {

    }

    // This belongs to ITestListener and will execute before starting of Test set/batch
    public void onStart(ITestContext arg0) {

    }

    // This belongs to ITestListener and will execute, once the Test set/batch is finished
    public void onFinish(ITestContext arg0) {

    }

    // This belongs to ITestListener and will execute only when the test is pass
    public void onTestSuccess(ITestResult arg0) {

        TestData testData = arg0.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestData.class);
        if (null == testData) {
            return;
        }
        addResultForCase(testData.id(), TEST_RAIL_RESULT_PASSED, getReporterResultsAsString(arg0));

    }

    private String getReporterResultsAsString(ITestResult arg0) {
        List<String> logResults = Reporter.getOutput(arg0);
        String returnString = "";

        for (String logEntry : logResults) {
            returnString += logEntry + "\n";
        }

        return returnString;
    }

    // This belongs to ITestListener and will execute only on the event of fail test
    public void onTestFailure(ITestResult arg0) {

        TestData testData = arg0.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestData.class);
        if (null == testData) {
            return;
        }
        addResultForCase(testData.id(), TEST_RAIL_RESULT_FAILED,
                getReporterResultsAsString(arg0) + "\n " + arg0.getThrowable().getMessage());

    }

    // This belongs to ITestListener and will execute before the main test start (@Test)
    public void onTestStart(ITestResult arg0) {

        TestData testData = arg0.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestData.class);
        if (null == testData) {
            return;
        }
        log.info(MessageFormat.format("\n >>>>>>>> TEST {0}: {1} <<<<<<<<", testData.id(), testData.description()));

    }

    // This belongs to ITestListener and will execute only if any of the main test(@Test) get skipped
    public void onTestSkipped(ITestResult arg0) {

    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {

    }

    // This belongs to IInvokedMethodListener and will execute before every method including @Before @After @Test
    public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {

    }

    // This belongs to IInvokedMethodListener and will execute after every method including @Before @After @Test
    public void afterInvocation(IInvokedMethod arg0, ITestResult arg1) {

    }

    // --- SEND REQUEST --- ////

    public void sendGetRequest(String getString) {

        log.info("\n---> send REQUEST:");

       // @formatter:off
       requestResponse = 
               given()
                   .header("Content-Type","application/json")
                   .auth()
                       .preemptive()
                           .basic(USER, PASSWORD)
                   .log().ifValidationFails()
               .get(API_URL + getString);
       // @formatter:on

        requestResponse.then().log().ifError();
        requestResponse.then().assertThat().statusCode(HttpStatus.SC_OK);

    }

    private void sendPostRequest(String apiMethod, String jsonAsString) {

        log.info("\n---> send REQUEST:");

        // @formatter:off
        requestResponse = 
                given()
                    .header("Content-Type","application/json")
                    .auth()
                        .preemptive()
                            .basic(USER, PASSWORD)
                    .contentType("application/json")
                    .body(jsonAsString)
                    .log().ifValidationFails()
                .when()
                    .post(API_URL + apiMethod);
        // @formatter:on

        requestResponse.then().log().ifError();
        requestResponse.then().assertThat().statusCode(HttpStatus.SC_OK);

    }

    // JSON PATH
    private String getJsonPathString(String path) {
        JsonPath responseInJson = requestResponse.jsonPath();
        return responseInJson.getString(path);
    }

    // TESTRAIL FUNCTIONS

    private void addRun() {
        // First if we have a testRailRunId we do nothing
        if (null != testRailRunId) {
            return;
        }

        // Second we try to get the latest automated open run id for this suite and use it
        String latestOpenRun = getLastOpenRunId();
        if (!latestOpenRun.equals("0")) {
            testRailRunId = latestOpenRun;
            log.info(MessageFormat.format("---> Using existing TestRail Run with ID: {0}", testRailRunId));
            return;
        }

        // If all else returns no run id candidate we create a new run
        LocalDateTime createTime = LocalDateTime.now();
        testRailRunCreationTime = createTime.toString();

        testRailRunName = "Automated run for Registration Server (ID: " + PROJECT_ID + ", Suite " + SUITE_ID + ") at "
                + testRailRunCreationTime;

        // @formatter:off
        String jsonAsString = 
                "{" 
                        + "\"suite_id\": " + SUITE_ID + "," 
                        + "\"name\": \"" + testRailRunName + "\","
                        + "\"description\": \"" + "Base Uri: " + RegistrationServerTestData.REGISTRATION_SERVER_BASE_URI + "\","
                        + "\"assignedto_id\": " + USER_ID + "," 
                        + "\"include_all\": true" 
                + "}";
        // @formatter:on

        log.info(MessageFormat.format("---> Adding TestRail Run: {0}", testRailRunName));
        sendPostRequest("add_run/" + PROJECT_ID, jsonAsString);

        testRailRunId = getJsonPathString("id");
        log.info(MessageFormat.format("---> Created TestRail Run with ID: {0}", testRailRunId));

    }

    public String getRunInfoAsString(String runId) {

        sendGetRequest("get_run/" + runId);
        return requestResponse.asString();

    }

    private String getLastOpenRunId() {

        Long unixTimeFromTwoDaysAgo = Instant.now().getEpochSecond() - (60 * 60 * 48);
        String timeForSearch = unixTimeFromTwoDaysAgo.toString();

        sendGetRequest("get_runs/" + PROJECT_ID + "&is_completed=0&created_by=" + USER_ID + "&suite_id=" + SUITE_ID
                + "&created_after=" + timeForSearch);

        String listOfRunsInResponse = getJsonPathString("id");

        listOfRunsInResponse = listOfRunsInResponse.replace("[", "").replaceAll("]", "");

        List<String> newList = Arrays.asList(listOfRunsInResponse.split("\\s*,\\s*"));

        Integer maxIdNumber = 0;

        for (String value : newList) {
            Integer valueInInt = 0;
            try {
                valueInInt = Integer.valueOf(value);
            } catch (NumberFormatException e) {

            }
            if (maxIdNumber < valueInInt) {
                maxIdNumber = valueInInt;
            }
        }

        return maxIdNumber.toString();

    }

    public void validateCase(String caseId) {
        String getString = "get_case/" + caseId;
        sendGetRequest(getString);
    }

    private void addResultForCase(String caseId, String statusId, String comment) {
        if (!ACTIVE.toLowerCase().contains("yes") || caseId.isEmpty()) {
            return;
        }

        addRun();

        comment = StringEscapeUtils.escapeJson(comment);

        // @formatter:off
        String jsonAsString = 
                "{" 
                        + "\"status_id\": " + statusId + "," 
                        + "\"comment\": \"" + comment + "\""
                + "}";
        // @formatter:on
        log.info(MessageFormat.format("---> adding result for test case {0}: {1}", caseId, jsonAsString));
        sendPostRequest("add_result_for_case/" + testRailRunId + "/" + caseId, jsonAsString);
    }

    public String getSuite(String suiteId) {
        sendGetRequest("get_suite/" + suiteId);
        return requestResponse.asString();

    }

}
