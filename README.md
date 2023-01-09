>ℹ️&nbsp;&nbsp;SignalFx was acquired by Splunk in October 2019. See [Splunk SignalFx](https://www.splunk.com/en_us/investor-relations/acquisitions/signalfx.html) for more information.

> # :warning: Deprecation Notice
> **The Splunk OpenTelemetry Java Lambda Wrapper has reached end of life ans has been permanently archived.**
> [The Splunk OpenTelemetry Lambda Layer](https://github.com/signalfx/splunk-otel-lambda) is the successor. To learn how to migrate, see  [the supporting documentation](https://docs.splunk.com/Observability/gdi/get-data-in/serverless/aws/otel-lambda-layer/instrument-lambda-functions.html#nav-Instrument-your-Lambda-function)

---

<p align="center">
  <strong>
    <a href="#getting-started">Getting Started</a>
    &nbsp;&nbsp;&bull;&nbsp;&nbsp;
    <a href="CONTRIBUTING.md">Contributing</a>
    &nbsp;&nbsp;&bull;&nbsp;&nbsp;
    <a href="#license">License</a>
    &nbsp;&nbsp;&bull;&nbsp;&nbsp;
    <a href="SECURITY.md">Security</a>
  </strong>
</p>

<p align="center">
  <a href="https://github.com/signalfx/splunk-otel-java-lambda/actions?query=workflow%3A%22PR+build%22">
    <img alt="Build Status" src="https://img.shields.io/github/workflow/status/signalfx/splunk-otel-java-lambda/PR%20build?style=for-the-badge">
  </a>
  <a href="https://github.com/signalfx/splunk-otel-java-lambda/releases">
    <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/signalfx/splunk-otel-java-lambda?include_prereleases&style=for-the-badge">
  </a>
</p>


<p align="center">
  <strong>
    <a href="TROUBLESHOOTING.md">Troubleshooting</a>
    &nbsp;&nbsp;&bull;&nbsp;&nbsp;
    <a href="RELEASING.md">Releasing</a>
    &nbsp;&nbsp;&bull;&nbsp;&nbsp;
    <a href="#logging">Logging</a>
    &nbsp;&nbsp;&bull;&nbsp;&nbsp;
    <a href="#examples">Examples</a>
  </strong>
</p>

---

# Splunk OpenTelemetry Java Lambda Wrapper

The Splunk OpenTelemetry Java Lambda is a modified version of the
wrappers in the [OpenTelemetry AWS Lambda
Instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/master/instrumentation/aws-lambda-1.0/library)
that enables you to export spans from an AWS Lambda function with Java to
Splunk APM without any code changes to your Lambda functions.

The current release uses `OpenTelemetry AWS Lambda Instrumentation` version
`1.1.0` and `OpenTelemetry Java SDK` version `1.1.0`.

This Splunk distribution comes with the following defaults:

- W3C specified Trace Context and Baggage propagation (`tracecontext,baggage`) context propagation
- OpenTelemetry Protocol (`otlp`) traces exporter
- No metrics exporter

This project contains the custom wrapper code in the [wrapper](https://github.com/signalfx/splunk-otel-java-lambda-wrapper/tree/main/wrapper)
directory and examples in the [examples](https://github.com/signalfx/splunk-otel-java-lambda-wrapper/tree/main/examples) directory.

There are two options to use the Splunk Lambda wrapper:

- Use a Lambda function wrapper directly
- Use a Lambda layer that Splunk hosts

Splunk provides a Serverless Application Model (SAM) template for deploying
the Lambda wrapper with a Lambda handler or a Lambda layer. If you choose
deploy the Lambda wrapper with a layer, Splunk also hosts a layer in AWS.

Outbound context propagation for lambdas instrumented with this wrapper can be
easily implemented. Please have a look [here](outbound-context-propagation.md).

## Getting Started

### Deploy the wrapper directly with a Lambda function handler

A Splunk Lambda wrapper wraps around an existing AWS Lambda Java function
handler. This approach doesn't require any code changes to your Lambda function.
When you deploy the Lambda wrapper with a Lambda handler, you add it as a
dependency to your Lambda function. Whenever the Lambda function is invoked,
it runs the Lambda wrapper which in turn calls your code. For more information
about AWS Lambda function handlers, see
[AWS Lambda function handler in Java](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
on the AWS website.

Follow these steps to configure a Splunk Lambda wrapper to export spans to
Splunk APM. You can also deploy the handler with a SAM
template. For more information, see the [example](./examples/splunk-wrapper/README.md).

1. Add the Splunk Lambda wrapper to your build definition:

   Gradle:
   ```
   dependencies {
     implementation("com.signalfx.public:otel-java-lambda-wrapper:0.0.13")
   }
   ```

   Maven:
   ```
   <dependency>
     <groupId>com.signalfx.public</groupId>
     <artifactId>otel-java-lambda-wrapper</artifactId>
     <version>0.0.13</version>
   </dependency>
   ```
2. From the AWS console, upload the .zip file to your Lambda function code.

   For more information, see [Deploy Java Lambda functions with .zip file archives](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html)
   on the AWS website.
3. Set a wrapper class as the handler for your Lambda function.

    These wrappers are available:

   | Wrapper class | Description |
   | ------------- | ----------- |
   | `com.splunk.support.lambda.TracingRequestWrapper` | Wrap a regular handler. |
   | `com.splunk.support.lambda.TracingRequestApiGatewayWrapper` | Wrap a regular handler proxied through an API Gateway. |
   | `com.splunk.support.lambda.TracingRequestStreamWrapper` | Wrap a streaming handler and enable HTTP context propagation for HTTP requests. |
   | `com.splunk.support.lambda.TracingSqsEventWrapper` | Wrap an AWS SQS Event handler. |

   For more information about setting a handler for your Lambda function in the AWS console, see [Configuring functions in the console](https://docs.aws.amazon.com/lambda/latest/dg/configuration-console.html) on the AWS website.
4. Set the `OTEL_INSTRUMENTATION_AWS_LAMBDA_HANDLER` environment variable in your Lambda function
   code:
   ```
   OTEL_INSTRUMENTATION_AWS_LAMBDA_HANDLER="package.ClassName::methodName"
   ```
   For more information about setting environment variables in the AWS console,
   see [Using AWS Lambda environment variables](https://docs.aws.amazon.com/lambda/latest/dg/configuration-envvars.html)
   on the AWS website.
5. By default, the Splunk Lambda wrapper uses W3C specified Trace Context and Baggage (`tracecontext,baggage`) context propagation.

   If you want to change this, set the `OTEL_PROPAGATORS` environment variable in your
   Lambda function code. For more information about available context
   propagators, see the [Propagator settings](https://github.com/open-telemetry/opentelemetry-java/tree/v1.1.0/sdk-extensions/autoconfigure#customizing-the-opentelemetry-sdk)
   for the OpenTelemetry Java.
6. By default, the Splunk Lambda wrapper uses the OpenTelemetry Protocol (`otlp`) exporter to send traces to Splunk APM.

   If you want to use this exporter, set these environment
   variables in your Lambda function code:
   ```
   OTEL_EXPORTER_OTLP_ENDPOINT="http://yourEndpoint:4317"
   SPLUNK_ACCESS_TOKEN="orgAccessToken"
   ```
   Also, you can set span flush wait timeout, that is max time the function will wait for the spans to be ingested by the Splunk APM. Default is 1 second.
   Timeout is controlled with a following property (value in milliseconds):
   ```
   OTEL_INSTRUMENTATION_AWS_LAMBDA_FLUSH_TIMEOUT: 30000
   ```

   If you want to use a different exporter, set the `OTEL_TRACES_EXPORTER`
   environment variable. Other exporters have their own configuration settings.
   For more information, see the [OpenTelemetry Java SDK](https://github.com/open-telemetry/opentelemetry-java/tree/v1.1.0/sdk-extensions/autoconfigure#customizing-the-opentelemetry-sdk) on GitHub.

   Splunk provides also token-authenticated `jaeger-thrift-splunk` exporter for customers that need to use that specific protocol. In order to use it, please set (example endpoint value for SmartAgent):
   ```
    OTEL_TRACES_EXPORTER=jaeger-thrift-splunk
    OTEL_EXPORTER_JAEGER_ENDPOINT=http://127.0.0.1:9080/v1/trace
    SPLUNK_ACCESS_TOKEN="orgAccessToken"
   ```
   You can also use the `jaeger-thrift-splunk` exporter to send spans directly to the Splunk Observability Cloud backend. You can accomplish this by updating OTEL_EXPORTER_JAEGER_ENDPOINT to the ingest URL.

   ```
    OTEL_TRACES_EXPORTER=jaeger-thrift-splunk
    OTEL_EXPORTER_JAEGER_ENDPOINT=https://ingest.<realm>.signalfx.com/v2/trace
    SPLUNK_ACCESS_TOKEN="orgAccessToken"
   ```

7. Set the environment in Splunk APM for the service with the
   `OTEL_RESOURCE_ATTRIBUTES` environment variable:
   ```
   OTEL_RESOURCE_ATTRIBUTES="environment=yourEnvironment"
   ```   
8. Set the service name in Splunk APM with the `OTEL_RESOURCE_ATTRIBUTES` environment variable:
    ```
    OTEL_RESOURCE_ATTRIBUTES="service.name=myServiceName
    ```
9. Save your settings and call the Lambda function.

### Deploy the wrapper with a Lambda layer

Add a layer that includes the Splunk Lambda wrapper to your Lambda function.
A layer is code and other content that you can run without including it in
your deployment package. Splunk provides layers in all supported regions you
can freely use.

You can also deploy the layer with a SAM template. For more information, see the
[example](./examples/splunk-layer/README.md).

To reduce the size of the deployment package, make sure that your Lambda
artifact doesn't contain the wrapper.

Follow these steps to configure a Splunk Lambda wrapper to export spans to
Splunk APM with a layer that Splunk provides.

1. From the AWS console, add a layer to your Lambda function code.
2. To add a layer that Splunk provides, specify an available ARN, depending on
   your region. For an available ARN, see
   [Latest available versions of SignalFx Lambda wrapper layers](https://github.com/signalfx/lambda-layer-versions).
3. Verify that dependencies in the layer aren't also in the Lambda function
   .jar file.
4. Deploy your Lambda function code.

## AWS span tags the wrapper adds to trace data

The Splunk Lambda wrapper automatically adds span tags to trace data it
exports. These are the available span tags for AWS metadata. For more
information, see the [OpenTelemetry Specification](https://github.com/open-telemetry/opentelemetry-specification).

| Span tag                                     | Example                                                                             | Description                                                                               |
| -------------------------------------------- | ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| `cloud.account.id`                           | `123456789012`                                                                      | The AWS account ID.                                                                       |
| `cloud.provider`                             | `aws`                                                                               | The name of the cloud provider.                                                           |
| `cloud.region`                               | `us-west-2`                                                                         | The AWS region.                                                                           |
| `faas.execution`                             | `af9d5aa4-a685-4c5f-a22b-444f80b3cc28`                                              | The AWS request ID.                                                                       |
| `faas.id`                                    | `arn:aws:lambda:us-west-2:123456789012:function:my-lambda-function`                 | The ARN of the Lambda function instance.                                                  |
| `faas.name`                                  | `my-lambda-function`                                                                | The Lambda function name.                                                                 |
| `faas.trigger`                               | `http`                                                                              | The type of trigger the function executed on. Only for the API gateway proxy.             |
| `faas.version`                               | `2.0.0`                                                                             | The Lambda function version.                                                              |
| `http.method`                                | `GET`, `POST`, `HEAD`                                                               | The HTTP request method. Only for the API gateway proxy.                                  |
| `http.url`                                   | `https://www.foo.bar/search?q=OpenTelemetry#SemConv`                                | The full HTTP request URL. Only for the API gateway proxy.                                |
| `http.user_agent`                            | `CERN-LineMode/2.15 libwww/2.17b3`                                                  | The value of the HTTP user-agent header the client sends. Only for the API gateway proxy. |
| `otel.library.name`                          | `io.opentelemetry.aws-lambda`                                                       | The SignalFx function wrapper qualifier.                                                  |
| `process.runtime.{name,version,description}` | `OpenJDK Runtime Environment,14.0.2,Eclipse OpenJ9 Eclipse OpenJ9 VM openj9-0.21.0` | The AWS execution environment.                                                            |

## Logging

These environment variables control logging:

| Environment variable | Description |
| -------------------- | ----------- |
| `OTEL_LIB_LOG_LEVEL` | Controls logging for the OpenTelemetry library. By default, it's set to `WARNING` and uses `java.util.logging` values. |
| `OTEL_LAMBDA_LOG_LEVEL` | Controls logging of the Splunk Lambda wrapper. By default, it's set to `WARN` and uses `log4j2` values.

## Examples

There are several examples provided in the `/examples` folder, grouped in following subfolders:
- `otel-wrapper` - examples using OpenTelemetry wrappers directly
- `splunk-layer` - examples using OpenTelemetry wrappers enhanced by Splunk, added as an AWS layer
- `splunk-wrapper` - examples using OpenTelemetry wrappers enhanced by Splunk, added as a direct dependency

Each group provides an AWS `template.yaml` to facilitate deployment and an extensive `README` documenting example calls with a relevant payload (in most cases - a simple string).

Refer to particular `README` files to check the details of provided examples.

## License

The Splunk OpenTelemetry Java Lambda Wrapper uses the
[OpenTelemetry Instrumentation for Java](https://github.com/open-telemetry/opentelemetry-java-instrumentation), [OpenTelemetry Java SDK and extensions](https://github.com/open-telemetry/opentelemetry-java),
all licensed under the terms of the Apache Software License version 2.0.
For more information, see the [license](./LICENSE) file.
