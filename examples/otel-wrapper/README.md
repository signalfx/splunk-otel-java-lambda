# Demo lambdas for OpenTelemetry tracing wrapper / handler

Documents how to use OTEL lambda wrappers. Most importantly shows that the user still needs to provide custom code in order to configure tracing. Here, it is configured for:
- B3 inbound propagation
- finest / debug logging
- logging span exporter (ie the spans will be captured in CW logs)

## Pre-requisites
* [AWS CLI](https://aws.amazon.com/cli/)
* [SAM CLI](https://github.com/awslabs/aws-sam-cli)
* [Gradle](https://gradle.org/) or [Maven](https://maven.apache.org/)

## Deployment

Edit `samconfig.toml` and set own values for:
- `stack_name`
- `s3_prefix` (or use `--resolve-s3` for deployment)

Run
```
$ sam build && sam deploy --resolve-s3
```

This command compiles the application and prepares a deployment package in the `.aws-sam` sub-directory. Then the package is deployed according to `samconfig.toml`

Once the deployment is completed, the SAM CLI will print out the stack's outputs, including the new application URL.

```
...
---------------------------------------------------------------------------------------------------------
OutputKey-Description                        OutputValue
---------------------------------------------------------------------------------------------------------
LambdaTestApi - URL for application            https://DEPLOYMENT_ID.execute-api.us-west-2.amazonaws.com/pets
---------------------------------------------------------------------------------------------------------

```

## Testing

### API gateway non-stream wrapper (hello API)
Wrapped function: `OtelApiGatewayRequestFunction`

Example call: `curl -v -H "X-B3-TraceId: 4fd0b6131f19f39af59518d127b0cafe" -H "X-B3-SpanId: 0000000000000456" -H "X-B3-Sampled: 1" -d "General Kenobi" https://DEPLOYMENT_ID.execute-api.us-east-2.amazonaws.com/Prod/hello`

### API gateway stream wrapper (pets API)
Wrapped function: `OtelApiGatewayRequestStreamFunction`

Example call: `curl -v -H "X-B3-TraceId: 4fd0b6131f19f39af59518d127b0cafe" -H "X-B3-SpanId: 0000000000000456" -H "X-B3-Sampled: 1" https://DEPLOYMENT_ID.execute-api.us-east-2.amazonaws.com/Prod/pets`

### Event based non-stream wrapper (String-typed lambda)
Wrapped function: `OtelRequestFunction`

Call lambda: `aws lambda invoke --function-name OtelRequestFunction --cli-binary-format raw-in-base64-out --payload '"General Kenobi"' out.txt`

### Event based stream wrapper (String-typed lambda)
Wrapped function: `OtelRequestStreamFunction`

Call lambda: `aws lambda invoke --function-name OtelRequestStreamFunction --cli-binary-format raw-in-base64-out --payload '"General Kenobi"' out.txt`

### Tips
Get lambda physical resource id: `aws cloudformation describe-stack-resource --stack-name PUT_STACK_NAME_HERE --logical-resource-id PUT_FUNCT_NAME_HERE --query 'StackResourceDetail.PhysicalResourceId' --output text`
