# Troubleshooting

A couple of notes what to consider in case of troubles:
1. Verifying configuration.

    Each lambda needs to be configured - consult [examples](./examples). 
    Pay attention to appropriate wrapper - depends on the handler type and trigger used (four different wrappers available).
    
2. Ensuring that traces are ingested.

    Wrapper will only wait configured (`OTEL_INSTRUMENTATION_AWS_LAMBDA_FLUSH_TIMEOUT`) number of milliseconds for backend to ingest the spans. Increase the value if you don't see the spans in the APM.

3. Verifying traces in the APM.

   Setting `OTEL_LAMBDA_LOG_LEVEL` to `DEBUG` will install logging exporter along with the configured one. Logging exporter will, in turn, log every span including traceId and spanId. TraceId can be then used in the APM to check if the trace was properly ingested. 
   
   The setting will also cause `jaeger-thrift-exporter` (if used) to log each trace sent to the APM along with the backend URL and token (masked) used by the operation.