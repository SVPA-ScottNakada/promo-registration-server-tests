
package com.promo.test.framework.utils;

import static io.restassured.RestAssured.given;

import com.promo.test.framework.utils.TestData;
import com.promo.test.suite.CommonTestData;

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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;

public class TestRailRequestHelper implements ITestListener, ISuiteListener, IInvokedMethodListener {

    static {
        System.setProperty("log4j.configurationFile", "var/secure/Log4j.properties");
    }

    protected Logger log = LogManager.getLogger(getClass());

    protected Response response = null;

    // TEST RAIL RUN INFO

    private String projectName = "";

    private String testRailProjectId = "";

    private String testRailSuiteId = "";

    private String runId = null;

    private String runName = "";

    private String runDescription = "";

    private List<String> completedTestCaseIds = new ArrayList<String>();

    // TEST RAIL END POINT AND USER

    private static final String ACTIVE = CommonTestData.TESTRAIL_ACTIVE;

    private static final String API_URL = CommonTestData.TESTRAIL_API_URL;

    private static final String USER = CommonTestData.TESTRAIL_USER;

    private static final String USER_ID = CommonTestData.TESTRAIL_USER_ID;

    private static final String PASSWORD = CommonTestData.TESTRAIL_PASSWORD;

    private static final Integer MAX_NUMBER_OF_RECORDS_RETURNED_BY_TESTRAIL = 250;

    private static final Integer MAX_VALID_TWO_DAY_RUN_SEARCH = 60 * 60 * 48;

    // TEST RAIL RESULT CODES

    protected static final String TEST_RAIL_RESULT_PASSED = "1";

    protected static final String TEST_RAIL_RESULT_BLOCKED = "2";

    protected static final String TEST_RAIL_RESULT_UNTESTED = "3";

    protected static final String TEST_RAIL_RESULT_RETEST = "4";

    protected static final String TEST_RAIL_RESULT_FAILED = "5";

    // CONSTRUCTOR

    public TestRailRequestHelper(String newProjectName, String newProjectId, String newSuiteId) {
        RestAssured.useRelaxedHTTPSValidation();
        projectName = newProjectName;
        testRailProjectId = newProjectId;
        testRailSuiteId = newSuiteId;
    }

    // ISUITE & ITEST FUNCTIONS

    // This belongs to ISuiteListener and will execute before the Suite start
    @Override
    public void onStart(ISuite arg0) {
        log.info(MessageFormat.format("\n >>>>>>>> TEST SUITE <<<<<<<< \n", arg0.getName()));
        String suitName = arg0.getXmlSuite().getName();

        runName = "Automated " + suitName + " run for " + projectName + " (ID: " + testRailProjectId + ", Suite "
                + testRailSuiteId + ")";
    }

    // This belongs to ISuiteListener and will execute, once the Suite is finished
    @Override
    public void onFinish(ISuite arg0) {
        cleanUpRunTestCaseIds();
    }

    // This belongs to ITestListener and will execute before starting of Test set/batch
    public void onStart(ITestContext arg0) {
        log.info(MessageFormat.format("\n >>>>>>>> TEST CLASS: {0} <<<<<<<< \n", arg0.getName()));
    }

    // This belongs to ITestListener and will execute, once the Test set/batch is finished
    public void onFinish(ITestContext arg0) {

    }

    // This belongs to ITestListener and will execute only when the test is pass
    public void onTestSuccess(ITestResult arg0) {

        log.info("... Success!\n");

        TestData testData = arg0.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestData.class);
        if (null == testData) {
            return;
        }
        addResultForCase(testData.id(), TEST_RAIL_RESULT_PASSED, getReporterResultsAsString(arg0));

    }

    // This belongs to ITestListener and will execute only on the event of fail test
    public void onTestFailure(ITestResult arg0) {

        log.warn("... Failure.\n");

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

    // GET REPORTER RESULT

    private String getReporterResultsAsString(ITestResult arg0) {
        List<String> logResults = Reporter.getOutput(arg0);
        String returnString = "";

        for (String logEntry : logResults) {
            returnString += logEntry + "\n";
        }

        return returnString;
    }

    // --- SEND REQUEST --- ////

    private void sendGetRequest(String getString) {
       // @formatter:off
       response = 
               given()
                   .header("Content-Type","application/json")
                   .auth()
                       .preemptive()
                           .basic(USER, PASSWORD)
               .get(API_URL + getString);
       // @formatter:on

        response.then().log().ifError();
        response.then().assertThat().statusCode(HttpStatus.SC_OK);

    }

    private void sendPostRequest(String apiMethod, String jsonAsString) {
        // @formatter:off
        response = 
                given()
                    .header("Content-Type","application/json")
                    .auth()
                        .preemptive()
                            .basic(USER, PASSWORD)
                    .contentType("application/json")
                    .body(jsonAsString)
                .when()
                    .post(API_URL + apiMethod);
        // @formatter:on

        response.then().log().ifError();
        response.then().assertThat().statusCode(HttpStatus.SC_OK);

    }

    // JSON PATH

    private String getJsonPathString(String path) {
        JsonPath responseInJsonPath = response.jsonPath();
        return responseInJsonPath.getString(path);
    }

    private List<String> getJsonPathStringList(String path) {
        JsonPath responseInJsonPath = null;
        try {
            responseInJsonPath = new JsonPath(response.then().extract().jsonPath().prettify());
        } catch (JsonPathException e) {
            log.warn("Response is not JSON");
        }
        return responseInJsonPath.getList(path);
    }

    private List<Integer> getJsonPathIntegerList(String path) {
        JsonPath responseInJsonPath = null;
        try {
            responseInJsonPath = new JsonPath(response.then().extract().jsonPath().prettify());
        } catch (JsonPathException e) {
            log.warn("Response is not JSON");
        }
        return responseInJsonPath.getList(path);
    }

    // SETS AND GETS

    protected String getRunDescription() {
        return runDescription;
    }

    protected void appendToRunDescription(String text) {
        if (!runDescription.isEmpty()) {
            runDescription += "\\n";
        }
        runDescription += text;

    }

    // TESTRAIL FUNCTIONS
    // documentation found on http://docs.gurock.com/testrail-api2/start

    // -- ADD RUN

    private void addRun() {
        // First if we have a testRailRunId we do nothing
        if (null != runId) {
            return;
        }

        // Second we try to get the latest automated open run id for this suite and use it
        String latestOpenRun = getLastOpenRunId();
        if (!latestOpenRun.equals("0")) {
            runId = latestOpenRun;
            log.info(MessageFormat.format("---> Using existing TestRail Run with ID: {0}", runId));
            return;
        }

        // If all else returns no run id candidate we create a new run
        LocalDateTime createTime = LocalDateTime.now();
        String runCreationTime = createTime.toString();

        runName = runName + " at " + runCreationTime;

        // @formatter:off
        String jsonAsString = 
                "{" 
                        + "\"suite_id\": " + testRailSuiteId + "," 
                        + "\"name\": \"" + runName + "\","
                        + "\"description\": \"" + runDescription + "\","
                        + "\"assignedto_id\": " + USER_ID + "," 
                        + "\"include_all\": true" 
                + "}";
        // @formatter:on

        log.info(MessageFormat.format("---> Adding TestRail Run: {0}", runName));
        sendPostRequest("add_run/" + testRailProjectId, jsonAsString);

        runId = getJsonPathString("id");
        log.info(MessageFormat.format("---> Created TestRail Run with ID: {0}", runId));

    }

    // -- GET LAST OPEN RUN'S ID

    private String getLastOpenRunId() {

        Long unixTimeFromTwoDaysAgo = Instant.now().getEpochSecond() - (MAX_VALID_TWO_DAY_RUN_SEARCH);
        String timeForSearch = unixTimeFromTwoDaysAgo.toString();

        sendGetRequest("get_runs/" + testRailProjectId + "&is_completed=0&created_by=" + USER_ID + "&suite_id="
                + testRailSuiteId + "&created_after=" + timeForSearch);

        List<Integer> listOfRunsInResponse = getJsonPathIntegerList("id");

        Integer maxIdNumber = 0;

        for (Integer value : listOfRunsInResponse) {
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

    // -- ADD RESULT

    private void addResultForCase(String caseId, String statusId, String comment) {

        if (!ACTIVE.equalsIgnoreCase("yes") || caseId.isEmpty()) {
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
        sendPostRequest("add_result_for_case/" + runId + "/" + caseId, jsonAsString);
        completedTestCaseIds.add(caseId);

    }

    // -- GET RUN'S RESULT COUNT

    public Integer getResultRunIdCount() {
        // get_results_for_run, helps us get the test Run id's in the latest test run
        // these ids are not the same as the test case's ids
        Integer totalNumberOfRecordsReturned = 0;
        List<String> totalReturnedIdList = new ArrayList<String>();
        Boolean getMoreIds = false;

        do {
            sendGetRequest("get_results_for_run/" + runId + "&offset=" + totalNumberOfRecordsReturned.toString());

            List<String> returnedRunIdList = getJsonPathStringList("test_id");
            Integer returnedRunIdCount = returnedRunIdList.size();

            totalReturnedIdList.addAll(returnedRunIdList);
            totalNumberOfRecordsReturned = totalNumberOfRecordsReturned + returnedRunIdCount;

            if (MAX_NUMBER_OF_RECORDS_RETURNED_BY_TESTRAIL.equals(returnedRunIdCount)) {
                getMoreIds = true;
            } else {
                getMoreIds = false;
            }
        } while (getMoreIds);

        // We remove duplicate test case's results
        Set<String> hs1 = new LinkedHashSet<>(totalReturnedIdList);
        List<String> noDuplicateResultsRunIds = new ArrayList<>(hs1);

        return noDuplicateResultsRunIds.size();
    }

    // UPDATE RUN'S TEST CASE INCLUSIONS

    private void cleanUpRunTestCaseIds() {

        // If no test case was executed we do nothing
        if (!ACTIVE.equalsIgnoreCase("yes") || completedTestCaseIds.size() == 0) {
            return;
        }

        // We make sure to have a valid testRailRunId to clean up
        if (null == runId) {
            runId = getLastOpenRunId();
        }

        if (runId.equals("0")) {
            log.warn("---> Didn't find valid open RunId");
            return;
        }

        // We only clean up the Run if the number of test results match the number of test cases executed
        Integer totalTestRailRunResults = getResultRunIdCount();
        Integer totalSuiteExecutedTestCases = completedTestCaseIds.size();
        if (!totalTestRailRunResults.equals(totalSuiteExecutedTestCases)) {
            log.warn("---> Number of test cases didn't match in clean up: TestRail -" + totalTestRailRunResults
                    + "-, Suite -" + totalSuiteExecutedTestCases + "-");
            return;
        }

        // update_run, we only include in the Run the test cases that were executed
        String postString = "update_run/" + runId;
        // @formatter:off
        String jsonAsString = 
                "{" 
                        + "\"include_all\": false" + "," 
                        + "\"case_ids\": " + completedTestCaseIds
                + "}";
        // @formatter:on
        sendPostRequest(postString, jsonAsString);
        log.info("---> TestRail run cleanup complete");
    }

}
