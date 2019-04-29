package io.slingr.endpoints.eversign;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by dgaviola on 04/06/18.
 */
public class WebhooksUtilsTest {
    @Test
    public void testSignatureValidation() {
        String eventTime = "1528138967";
        String eventType = "document_sent";
        String eventHash = "20d013adac25d52959e997e65a44bfe404924f31a96d739365a41462671731f6";
        String apiKey = "bed2f1f207e0d0a5b6986b5844858729";
        assertTrue(WebhooksUtils.verifySignature(eventTime+eventType, eventHash, apiKey));
    }
}
