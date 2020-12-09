package com.splunk.support.lambda.examples;

public class TracingRequestWrapper extends io.opentelemetry.instrumentation.awslambda.v1_0.TracingRequestWrapper {
    static {
        LambdaConfiguration.configure();
    }
}
