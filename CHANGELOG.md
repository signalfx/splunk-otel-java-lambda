# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### General
- Updated to the newest OpenTelemetry Java Instrumentation [v1.1.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.1.0) and OpenTelemetry Java SDK [v1.1.0](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v1.1.0).

### Enhancements
- Configured the OTEL wrapper example to disable not needed resource providers.
- Configured disabled resource providers to match OTEL format (`ResourceProvider` instead of `Resource`).

-----
## [0.0.4] - 04.03.2021

### General
- Updated to the newest OpenTelemetry Java Instrumentation [v0.17.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v0.17.0) and OpenTelemetry Java SDK [v0.17.1](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.17.1).

-----
## [0.0.3] - 28.01.2021

### General
- Updated README.md with current env var names
- Updated examples with current env var names
- Upstream OpenTelemetry Java Instrumentation 0.14.0 contains an issue that prevents HTTP context propagation. Therefore, only XRay context propagation works currently.

### Enhancements
- "always on" sampler configured - all spans will be always exported
- Removed HTTP interceptor logging

------
## [0.0.2] - 26.01.2021

### General
- Updated to the newest OpenTelemetry Java Instrumentation [v0.14.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v0.14.0) and OpenTelemetry Java SDK [v0.14.1](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.14.1).
- Improved and extended README.md.

### Enhancements
- Ability to configure wrapper max ingest wait timeout with `OTEL_INSTRUMENTATION_AWS_LAMBDA_FLUSH_TIMEOUT`.
- Additional logging for `jaeger-thrift` exporter controlled by OTEL library log level variable `OTEL_LIB_LOG_LEVEL`.
- Added support for additional W3CBaggagePropagator (`baggage`).

------
## [0.0.1] - 28.12.2020

### General
- First release of the wrapper. 
- Configures Splunk defaults for export and propagation.
- Provides a wide variety of examples.
- Uses OpenTelemetry Java Instrumentation [v0.11.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v0.11.0) and OpenTelemetry Java SDK [v0.11.0](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.11.0).

### Enhancements
- Provides propagator configuration via env props.
- Provides exporter configuration via env props.
- Supports direct Splunk cloud ingest via Jaeger / Thrift over http exporter.
  
