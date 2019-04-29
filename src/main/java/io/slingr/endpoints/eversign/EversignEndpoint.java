package io.slingr.endpoints.eversign;

import io.slingr.endpoints.HttpEndpoint;
import io.slingr.endpoints.exceptions.EndpointException;
import io.slingr.endpoints.exceptions.ErrorCode;
import io.slingr.endpoints.framework.annotations.*;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.services.rest.RestMethod;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.ws.exchange.FunctionRequest;
import io.slingr.endpoints.ws.exchange.WebServiceRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eversign endpoint
 *
 * Created by dgaviola on 11/13/17.
 */
@SlingrEndpoint(name = "eversign", functionPrefix = "_")
public class EversignEndpoint extends HttpEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(EversignEndpoint.class);

    private static final String EVERSIGN_API_URL = "https://api.eversign.com/api";

    @ApplicationLogger
    private AppLogs appLogger;

    @EndpointProperty
    private String apiKey;

    @EndpointProperty
    private String businessId;

    @EndpointProperty
    private String sandboxMode;

    @Override
    public String getApiUri() {
        return EVERSIGN_API_URL;
    }

    @Override
    public void endpointStarted() {
        httpService().setAllowExternalUrl(true);
    }

    @EndpointFunction(name = "_get")
    public Json get(FunctionRequest request) {
        logger.info(String.format("GET [%s]", request.getJsonParams().string("path")));
        setRequestConfig(request, false);

        final Json res = defaultGetRequest(request);
        checkUnsuccessfulResponse(request, res);
        return res;
    }

    @EndpointFunction(name = "_post")
    public Json post(FunctionRequest request) {
        logger.info(String.format("POST [%s]", request.getJsonParams().string("path")));
        setRequestConfig(request, true);

        final Json res = defaultPostRequest(request);
        checkUnsuccessfulResponse(request, res);
        return res;
    }

    @EndpointFunction(name = "_delete")
    public Json delete(FunctionRequest request) {
        logger.info(String.format("DELETE [%s]", request.getJsonParams().string("path")));
        setRequestConfig(request, false);

        final Json res = defaultDeleteRequest(request);
        checkUnsuccessfulResponse(request, res);
        return res;
    }

    @EndpointWebService(path = "/", methods = RestMethod.POST)
    public String webhookProcessor(WebServiceRequest request){
        Json body = request.getJsonBody();
        String signature = body.string("event_hash");
        String eventTime = body.string("event_time");
        String eventType = body.string("event_type");
        logger.info(String.format("Webhook [%s] coming from Eversign", eventType));
        if (!WebhooksUtils.verifySignature(eventTime + eventType, signature, apiKey)) {
            logger.warn(String.format("Invalid signature for hash [%s], time [%s] and type [%s]", signature, eventTime, eventType));
            throw EndpointException.permanent(ErrorCode.ARGUMENT, "Invalid signature");
        }
        events().send("webhook", body);
        return "ok";
    }

    private void setRequestConfig(FunctionRequest request, boolean post) {
        Json body = request.getJsonParams();
        Json params = body.json("params");
        if (params == null) {
            params = Json.map();
        }
        params.set("access_key", apiKey);
        if (!StringUtils.isBlank(businessId)) {
            params.set("business_id", businessId);
        }
        body.set("params", params);
        if (post && "yes".equalsIgnoreCase(sandboxMode)) {
            Json content = body.json("body");
            if (content == null) {
                content = Json.map();
            }
            content.set("sandbox", 1);
            body.set("body", content);
        }
    }

    private void checkUnsuccessfulResponse(FunctionRequest request, Json res) {
        if (res != null && res.contains("success") && !res.bool("success")) {
            // error from api
            final String error = getErrorMessage(res);

            logger.warn(String.format("Error making request to [%s]: %s - %s", request.getJsonParams().string("path"), error, res.toString()));
            throw EndpointException.permanent(ErrorCode.API, String.format("Error returned by the Eversign API: %s", error), res);
        }
    }

    private String getErrorMessage(Json res) {
        String error = null;
        if(res != null) {
            try {
                final Json e = res.json("error");
                if (e != null) {
                    error = e.string("info");
                    if(StringUtils.isBlank(error)) {
                        error = e.string("type");
                    }
                }
            } catch (Exception ex) {
                logger.warn(String.format("Error processing error response for [%s]: %s", res.toString(), ex.getMessage()));
            }
        }
        if(StringUtils.isBlank(error)){
            error = "-";
        }
        return error;
    }
}
