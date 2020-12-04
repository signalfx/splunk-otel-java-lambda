package com.splunk.support.lambda.examples;

public class TracingRequestApiGatewayWrapper extends io.opentelemetry.instrumentation.awslambda.v1_0.TracingRequestApiGatewayWrapper{
    static {
        LambdaConfiguration.configure();
    }
}
