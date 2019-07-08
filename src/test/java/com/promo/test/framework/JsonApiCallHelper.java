
package com.promo.test.framework;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;

public class JsonApiCallHelper extends BaseApiCallHelper {

    private JsonPath responseInJsonPath = null;

    public JsonApiCallHelper(String newUri) {
        super(newUri);
        RestAssured.defaultParser = Parser.JSON;
        setContentType("application/json");
    }

    // --- SEND REQUEST --- ////

    /*
     * (non-Javadoc)
     * @see com.biv.test.framework.BaseApiCallHelper#sendGetRequest(java.lang.Boolean)
     */
    @Override
    protected void sendGetRequest(Boolean followRedirects) {
        super.sendGetRequest(followRedirects);
        setResponseAsJsonPath();
    }

    /*
     * (non-Javadoc)
     * @see com.biv.test.framework.BaseApiCallHelper#sendPostRequest()
     */
    @Override
    protected void sendPostRequest() {
        super.sendPostRequest();
        setResponseAsJsonPath();
    }

    private void setResponseAsJsonPath() {
        // The content type could have been changed, so we only extract if its still Json
        if (getContentType().equals("application/json")) {
            try {
                responseInJsonPath = new JsonPath(response.body().asString());
            } catch (JsonPathException e) {
                log.warn("---> Response is not JSON");
            }
        }
    }

    // --- RESPONSE'S PATH INFO METHODS --- ////
    /**
     * Returns the value of a path in the response.
     * If it doesn't find the path it returns an empty string.
     *
     * @param pathForValue string for the path.
     * @return string with the extracted value.
     */
    public String getPathValue(String pathForValue) {
        return getPathValue(pathForValue, false, true);
    }

    /**
     * Returns the value of a path in the response.
     * 
     * @param pathForValue string for the path.
     * @param returnNull if set to false function will return an empty string instead of null.
     * @param logMessage set to true if a log message is needed.
     * @return
     */
    protected String getPathValue(String pathForValue, Boolean returnNull, Boolean logMessage) {
        String actualValue = responseInJsonPath.getString(pathForValue);
        if (null == actualValue) {
            if (!returnNull) {
                actualValue = "";
            }
        }
        if (logMessage) {
            logToReport(MessageFormat.format("For path -{0}- returning value -{1}-", pathForValue, actualValue));
        }
        return actualValue;
    }

    /**
     * Returns a path as a list.
     * If the the path doesn't contain a list it will return a list containing one item with the getString value.
     * If the path isn't found an empty list will be returned instead.
     * 
     * @param pathForList
     * @return
     */
    private List<Object> getListFromJsonPath(String pathForList) {
        List<Object> listToReturn = new ArrayList<Object>();

        // First we attempt the getString to check if the path is found
        String getStringFromJsonPath = getPathValue(pathForList, true, false);
        if (null == getStringFromJsonPath) {
            // We return an empty list
            log.debug("---> Couldnt find path for list -" + pathForList + "-");
            return listToReturn;
        }

        // Next we try to extract a list from the path
        try {
            List<Object> actualGetListFromJsonPath = responseInJsonPath.getList(pathForList);
            // JsonPath has a bad habit of adding null values
            // for list paths were the parent node exists but the end node doesn't
            actualGetListFromJsonPath.removeAll(Collections.singleton(null));
            listToReturn = actualGetListFromJsonPath;

        } catch (ClassCastException e) {
            // If the getList was unsuccessful we make the getStringFromJsonPath the only value in the list
            listToReturn.add(getStringFromJsonPath);
        }
        return listToReturn;
    }

    // --- VALIDATIONS --- ////

    /**
     * Validates that a path has an expected value in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValue(String pathToValidate, String expectedValue) {
        String actualValue = getPathValue(pathToValidate, true, false);
        logToReport(MessageFormat.format("Validating -{0}-, expected value -{1}-, actual value -{2}-", pathToValidate,
                expectedValue, actualValue));
        assertThat(actualValue, equalTo(expectedValue));
    }

    /**
     * Validates that a value is present in one of the many possible instances of a path in the response.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedValue string for the value to verify.
     */
    public void validateValueInList(String pathToValidate, Object expectedValue) {
        List<Object> listOfValues = getListFromJsonPath(pathToValidate);
        logToReport(MessageFormat.format("Validating -{0}- list of values, expected -{1}- in list -{2}-",
                pathToValidate, expectedValue, listOfValues));
        assertThat(listOfValues, hasItems(expectedValue));
    }

    /**
     * Given a path in the response, validates the number of objects/instances found.
     *
     * @param pathToValidate string for the path to validate.
     * @param expectedCount integer for expected count.
     */
    public void validatePathListCount(String pathToValidate, Integer expectedCount) {
        List<Object> listOfValues = getListFromJsonPath(pathToValidate);
        Integer actualCount = listOfValues.size();
        logToReport(MessageFormat.format("Validating -{0}- list count, expected -{1}-, actual -{2}-", pathToValidate,
                expectedCount, actualCount));
        assertThat(actualCount, equalTo(expectedCount));
    }

    /**
     * Validates that a response's path value is not null or empty.
     *
     * @param pathToValidate string for the path to validate.
     */
    public void validateNotNullOrEmpty(String pathToValidate) {
        logToReport(MessageFormat.format("Validating -{0}- not null or empty", pathToValidate));
        String actualValue = getPathValue(pathToValidate, true, true);
        assertThat(actualValue, not(isEmptyOrNullString()));
    }

    // --- MAP TO JSON STRING--- ////

    protected String simpleMapToJsonString(Map<String, Object> mapToConvert) {
        Map<String, Object> mapWithQuotes = new LinkedHashMap<String, Object>();

        for (Map.Entry<String, Object> mapEntry : mapToConvert.entrySet()) {
            String key = "\"" + mapEntry.getKey() + "\"";
            String value = mapEntry.getValue().toString();
            if (mapEntry.getValue().getClass().getTypeName().contains("String")) {
                value = "\"" + value + "\"";
            }
            mapWithQuotes.put(key, value);
        }
        return simpleMapToString(mapWithQuotes, "{", ": ", ",", "}");
    }

}
