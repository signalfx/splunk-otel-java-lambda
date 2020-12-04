package com.splunk.support.lambda.examples;

public class TracingRequestStreamWrapper extends io.opentelemetry.instrumentation.awslambda.v1_0.TracingRequestStreamWrapper {
    static {
        LambdaConfiguration.configure();
    }
}
