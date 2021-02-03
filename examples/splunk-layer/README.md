# Demo lambdas for tracing wrapper / handler testing

Documents how to use Splunk lambda wrappers with layer, minimizing deployment size.
Most importantly shows that no custom code is needed, as configuration can be set via env properties. in this example, following values are configured:
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

## Testing

### Event based non-stream wrapper (String-typed lambda)

Wrapped function: `RequestFunctionWithLayer`

Call lambda: `aws lambda invoke --function-name RequestFunctionWithLayer --cli-binary-format raw-in-base64-out --payload '"General Kenobi"' out.txt`
