# Troubleshooting

A couple of notes what to consider in case of troubles:
- wrapper needs to be configured - consult [examples](./examples)
- wrapper will only wait configured (`OTEL_INSTRUMENTATION_AWS_LAMBDA_FLUSH_TIMEOUT`) number of milliseconds for backend 
to ingest spans
- setting `OTEL_LAMBDA_LOG_LEVEL` to `debug` will install logging exporter along with the configured one