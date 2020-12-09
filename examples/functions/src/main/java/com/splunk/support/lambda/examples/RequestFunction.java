package com.splunk.support.lambda.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class RequestFunction implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String input, Context context) {
        System.out.println("RequestWrapperFunction: "+input);
        return "Hi "+input;
    }
}
