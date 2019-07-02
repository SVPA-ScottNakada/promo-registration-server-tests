
package com.promo.test.framework;

public class SimpleXmlApiCallHelper extends XmlApiCallHelper {

    public SimpleXmlApiCallHelper(String newUri) {
        super(newUri);
    }

    // Send Request
    public void sendGetRequest() {
        super.sendGetRequest();
    }
    
    public void sendGetRequestNoRedirect() {
        super.sendGetRequest(false);
    }

    public void sendPostRequest() {
        super.sendPostRequest();
    }
}
