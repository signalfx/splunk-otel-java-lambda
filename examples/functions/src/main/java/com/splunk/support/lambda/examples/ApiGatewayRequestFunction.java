package com.splunk.support.lambda.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class ApiGatewayRequestFunction implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

    @Override
    public Object handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        System.out.println("RequestApiGatewayWrapperFunction: "+input.getBody());
        return new APIGatewayProxyResponseEvent().withBody("Hi "+input.getBody()).withStatusCode(200);
    }
}
