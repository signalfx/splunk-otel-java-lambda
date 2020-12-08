# Splunk OpenTelemetry Java Lambda Wrappers

## Structure
- `wrapper` contains custom Splunk code for lambda wrapper and lambda configuration
- `examples` contains following:
  - `splunk-wrapper` - showing how to use Splunk OTEL Java Lambda Wrapper directly
  - `splunk-layer` - showing how to use Splunk OTEL Java Lambda Wrapper with layer 
  - `otel-wrapper` - showing how to use bare OTEL Java Lambda Wrapper directly

## Overview

You can use this document to learn how to use Splunk OpenTelemetry (OTEL) Java Lambda Wrapper to instrument your AWS lambdas.

OTEL Java Lambda Wrappers wrap around an AWS Lambda Java function handler, which allows traces to be exported from Java Lambda functions. This approach does not require code changes to existing software.

OTEL Java Lambda Handlers can be extended to add tracing capabilities to new lambdas. This approach requires code changes.

Splunk provides extensions to OTEL wrappers, allowing for cheap adoption of tracing in the Java lambda world. These wrappers can be configured entirely by AWS environment properties. Following classes are available:
- `com.splunk.support.lambda.TracingRequestApiGatewayWrapper` - for wrapping regular handlers (implementing `RequestHandler`) proxied through API Gateway, enabling
- `com.splunk.support.lambda.TracingRequestStreamWrapper` - for wrapping streaming handlers (implementing `RequestStreamHandler`), enabling HTTP context propagation for HTTP requests 
- `com.splunk.support.lambda.TracingRequestWrapper` - for wrapping regular handlers (implementing `RequestHandler`)

### Inbound context propagation

HTTP headers based context propagation is supported for API Gateway (HTTP) requests. In order to enabled it, please wrap your lambda with either `TracingRequestStreamWrapper` or `TracingRequestApiGatewayWrapper`

Supported propagators are documented here: https://github.com/open-telemetry/opentelemetry-java/tree/master/extensions/trace_propagators

Configuration section and examples show how to set desired propagator.

### Outbound context propagation


## Usage step 1: Install via Maven

```
<dependency>
  <groupId>com.splunk.public</groupId>
  <artifactId>otel-lambda-wrapper</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage step 2: Wrap lambda function 

Configure `OTEL_LAMBDA_HANDLER` env property to your lambda handler method in following format `package.ClassName::methodName` and use one of wrappers as your lambda `Handler` (in `template.yaml`).

Further installation instructions, including ways to use handlers, can be found [here](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/master/instrumentation/aws-lambda-1.0/library).

To reduce the size of the deployment package, make sure that your Lambda artifact does not contain the wrapper (no direct Maven or Gradle dependency upon wrapper artfact). 

## Usage step 3: Set environment variables for the Lambda function

### Configure propagation 

Set `OTEL_PROPAGATORS` variable to list of required propagators. `b3` propagator is the default one (if none configured).

### Configure exporter

Set `OTEL_EXPORTERS` variable to list of required propagators. `jaeger-thrift` is the default one (if none configured).

Particular exporters have own configuration variables - please see [OTEL Java instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation) for more details. Jaeger over thrift (default one) uses following properties:
- `OTEL_EXPORTER_JAEGER_ENDPOINT` - tracing endpoint - default value set to `http://localhost:9080/v1/trace` 
- `OTEL_EXPORTER_JAEGER_SERVICE_NAME` - name of the service - default value set to `OtelInstrumentedLambda`
- `SIGNALFX_AUTH_TOKEN` - auth token if communicating with Splunk cloud, passed as `X-SF-TOKEN` header - default is empty

If communicating directly with Splunk cloud, please set `environment` for better visibility of your traces in the UI. This can be done by configuring `OTEL_RESOURCE_ATTRIBUTES` variable value in a following manner: `environment=<YOUR ENVIRONMENT>`

### Logging

Following variables can be used to control logging:
- `OTEL_LIB_LOG_LEVEL` controls logging of the OTEL library itself, set to `WARNING` by default (`java.util.logging` values)
- `OTEL_LAMBDA_LOG_LEVEL` controls logging of the Splunk wrapper, set to `WARN` by default (`log4j2` values)

## Using layer

For advanced users who want to reduce the size of deployment packages, Splunk provides AWS layer.

At a high level, to reduce the size of deployments with AWS Lambda layers, you need to:

1. Determine the layer to use. There are two options:
   - Option 1: Layer hosted by Splunk
     - You can use the version of the layer hosted by Splunk. Available hosted layers may differ based on region. To review the latest available version based on your region, please see the [list of supported versions](https://github.com/signalfx/lambda-layer-versions/blob/master/otel-java/OTEL-JAVA.md).
   - Option 2: SAM (Serverless Application Model) template
     - You can deploy a copy of the Splunk-provided layer to your account. Splunk provides a SAM template that will create a layer with the wrapper in your AWS account.
     - To use this option, log into your AWS account. In the Lambda section, create a function, and then choose the option to create a function from a template. Search for Splunk, choose OTEL Java, and then deploy.
     - You can also locate the Splunk layer using the Serverless Application Repository service.
2. Verify that dependencies included in the layer are not included in the Lambda .jar file.
3. Attach the layer to the Lambda function.


## Additional documentation

- Generic OTEL Java lambda wrappers and handlers - [how to use and configure](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/master/instrumentation/aws-lambda-1.0/library).
- Trace exporters and propagators as supported by [OTEL Java instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation) 
- Information on attaching [an AWS layer to a Lambda](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html#configuration-layers-using).

# License and versioning

The Splunk distribution of OpenTelemetry Lambda Java Wrappers uses the [OpenTelemetry Java Instrumentation
project](https://github.com/open-telemetry/opentelemetry-java-instrumentation).
It is released under the terms of the Apache Software License version 2.0. See
[the license file](./LICENSE) for more details.