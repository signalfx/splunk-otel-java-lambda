# Splunk OpenTelemetry Java Lambda Wrapper

The Splunk OpenTelemetry Java Lambda Wrapper is a modified version of the [OpenTelemetry AWS Lambda Instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/master/instrumentation/aws-lambda-1.0/library) that enables you to export spans from an AWS Lambda function with Java to Splunk APM without any code changes to your Lambda functions.

The Splunk Lambda wrapper uses b3 context propagation and a jaeger-thrift exporter to send trace metadata to Splunk APM. If needed, you can customize context propagation and the exporter when you deploy the wrapper.

There are two options to deploy the Splunk Lambda wrapper:

- Add a Lambda function handler
- Use a Lambda layer

Splunk provides a Serverless Application Model (SAM) template for deploying the Lambda wrapper with a Lambda handler or a Lambda layer. If you choose deploy the Lambda wrapper with a layer, Splunk also hosts a layer in AWS.

## Deploy the wrapper with a Lambda function handler

A Splunk Lambda wrapper uses an existing AWS Lambda Java function handler. This approach doesn't require any code changes to your Lambda function. When you deploy the Lambda wrapper with a Lambda handler, you add it as a dependency to your Lambda function. Whenever the Lambda function is invoked, it runs the Lambda wrapper. For more information about AWS Lambda function handlers, see [AWS Lambda function handler in Java](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html) on the AWS website.

To reduce the size of the deployment package, make sure that your Lambda artifact doesn't contain the wrapper.

Follow these steps to configure a Splunk Lambda wrapper to export spans to Splunk APM from the AWS console. You can also deploy the handler with a SAM template. For more information, see the [example](./examples/splunk-wrapper/README.md). 

1. Add the Splunk Lambda wrapper to your build definition:

   Gradle:
   ```
   dependencies {
     implementation("com.splunk.public:otel-lambda-wrapper:1.0.0")
   }
   ```

   Maven:
   ```
   <dependency>
     <groupId>com.splunk.public</groupId>
     <artifactId>otel-lambda-wrapper</artifactId>
     <version>1.0.0</version>
   </dependency>
   ```
2. From the AWS console, upload the .zip file to your Lambda function code. For more information, see [Deploy Java Lambda functions with .zip file archives](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html) on the AWS website.
3. Set a wrapper class as the handler for your Lambda function. These wrappers are available:
   | Wrapper class | Description |
   | ------------- | ----------- |
   | `com.splunk.support.lambda.TracingRequestWrapper` | Wrap a regular handler. |
   | `com.splunk.support.lambda.TracingRequestApiGatewayWrapper` | Wrap a regular handler proxied through an API Gateway. |
   | `com.splunk.support.lambda.TracingRequestStreamWrapper` | Wrap a streaming handler and enable HTTP context propagation for HTTP requests. |
   For more information about setting a handler for your Lambda function in the AWS console, see [Configuring functions in the console](https://docs.aws.amazon.com/lambda/latest/dg/configuration-console.html) on the AWS website.
4. Set the `OTEL_LAMBDA_HANDLER` environment variable in your Lambda function code:
   ```
   OTEL_LAMBDA_HANDLER="package.ClassName::methodName"
   ```
   For more information about setting environment variables in the AWS console, see [Using AWS Lambda environment variables](https://docs.aws.amazon.com/lambda/latest/dg/configuration-envvars.html) on the AWS website.
5. By default, the Splunk Lambda wrapper uses B3 context propagation. If you want to change this, set the `OTEL_PROPAGATORS` environment variable in your Lambda function code. For more information about available context propagators, see the [Propagator settings](https://github.com/open-telemetry/opentelemetry-java-instrumentation#propagator) for the OpenTelemetry Java Instrumentation.
6. By default, the Splunk Lambda wrapper uses a jaeger-thrift exporter to send traces to Splunk APM. If you want to use this exporter, set these environment variables in your Lambda function code:
   ```
   OTEL_EXPORTER_JAEGER_ENDPOINT="http://yourEndpoint:9080/v1/trace"
   OTEL_EXPORTER_JAEGER_SERVICE_NAME="serviceName"
   SIGNALFX_AUTH_TOKEN="orgAccessToken"
   ```
   If you want to use a different exporter, set the `OTEL_EXPORTERS` environment variable. Other exporters have their own configuration settings. For more information, see the [OpenTelemetry Instrumentation for Java](https://github.com/open-telemetry/opentelemetry-java-instrumentation) on GitHub.
7. Set the environment in Splunk APM for the service with the `OTEL_RESOURCE_ATTRIBUTES` environment variable:
   ```
   OTEL_RESOURCE_ATTRIBUTES="environment=yourEnvironment"
   ```
8. Deploy your Lambda function code.

## Deploy the wrapper with a Lambda layer

Add a layer that includes the Splunk Lambda wrapper to your Lambda function. A layer is code and other content that a Lambda function that you can run without including them in your deployment package. Splunk provides layers you can deploy. 

Follow these steps to configure a Splunk Lambda wrapper to export spans to Splunk APM with a layer that Splunk provides. You can also deploy the layer with a SAM template. For more information, see the [example](./examples/splunk-layer/README.md). 

1. From the AWS console, add a layer to your Lambda function code.
2. To add a layer that Splunk provides, specify an available ARN, depending on your region:
   | Region | ARN |
   | ------ | --- |
   | us-west-1 | arn:aws:lambda:us-west-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | us-west-2 | arn:aws:lambda:us-west-2:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | us-east-1 | arn:aws:lambda:us-east-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | us-east-2 | arn:aws:lambda:us-east-2:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | eu-west-1 | arn:aws:lambda:eu-west-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | eu-west-2 | arn:aws:lambda:eu-west-2:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | eu-west-3 | arn:aws:lambda:eu-west-3:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | eu-north-1 | arn:aws:lambda:eu-north-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | eu-central-1 | arn:aws:lambda:eu-central-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | ap-south-1 | arn:aws:lambda:ap-south-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | ap-southeast-1 | arn:aws:lambda:ap-southeast-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | ap-southeast-2 | arn:aws:lambda:ap-southeast-2:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | ap-northeast-1 | arn:aws:lambda:ap-northeast-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | ap-northeast-2 | arn:aws:lambda:ap-northeast-2:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | ca-central-1 | arn:aws:lambda:ca-central-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
   | sa-east-1 | arn:aws:lambda:sa-east-1:254067382080:layer:signalfx-lambda-java-wrapper:2 |
3. Verify that dependencies in the layer aren't also in the Lambda function .jar file.
4. Deploy your Lambda function code.

## Logging

These environment variables control logging:

| Environment variable | Description |
| -------------------- | ----------- |
| `OTEL_LIB_LOG_LEVEL` | Controls logging for the OpenTelemetry library itself. By default, it's set to `WARNING` and uses `java.util.logging` values. |
| `OTEL_LAMBDA_LOG_LEVEL` | Controls logging of the Splunk Lambda wrapper. By default, it's set to `WARN` and uses `log4j2` values.
  
## License and versioning

The Splunk OpenTelemetry Java Lambda Wrapper uses the [OpenTelemetry Instrumentation for Java](https://github.com/open-telemetry/opentelemetry-java-instrumentation), which is released under the terms of the Apache Software License version 2.0. For more information, see the [license](./LICENSE) file.


