# Outbound context propagation

As the wrapper does not instrument lambda function code, it's not possible to have an implicit outbound context propagation. That is trace context (trace id and parent span id) **will not** be automatically propagated with outgoing HTTP calls. 

## Manual instrumentation

However, it is still possible to have an outbound context propagation with a bit of custom code.  In order to simplify this, Splunk provides a small utility class - `com.splunk.support.lambda.PropagationHelper`.

The class provides single static method - `createHeaders`, which returns name-to-value map of HTTP headers that should be added to an outgoing request. 

## Usage

1. If you are using Splunk wrapper directly (as a build dependency - Maven or Gradle), you don't need to do anything, helper is available to use.
2. If you are using Splunk hosted lambda layer (no build dependency to the Splunk wrapper), you need to add the wrapper as a dependency that will not be included in the resulting binary but will be available in the compilation time.
   - for Maven - `provided`
   - for Gradle - `compileOnly`
   - for example:
   ```
      dependencies {
        compileOnly('com.signalfx.public:otel-java-lambda-wrapper:0.0.5')
      }
   ```

## Example

Exemplary code adding headers to standard Java `HttpUrlConnection` looks as follows:

```
    private void addPropagationHeaders(HttpURLConnection urlConnection) {
        Map<String, String> propagationHeaders = PropagationHelper.createHeaders();
        for (Map.Entry<String, String> header : propagationHeaders.entrySet()) {
            urlConnection.setRequestProperty(header.getKey(), header.getValue());
        }
    }
```