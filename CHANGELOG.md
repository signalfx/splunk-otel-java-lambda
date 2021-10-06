# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.0.11] - 2021-10-06

### Bugfixes
- Fixed issue with AWS lambda logging dependencies not included if the wrapper is used as a direct dependency

### Enhancements
- Added info message if logging exporter is installed for debug purposes
- Default value of the configuration property `otel.lib.log.level` set to `INFO`

## [0.0.10] - 2021-10-05

### Bugfixes
- Fixed issue with missing `opentelemetry-extension-aws` module, preventing trace propagation from XRay.

### Enhancements
- Set `jaeger-thrift-splunk` endpoint URL default value, enabling usage of the library even with generic wrappers. 

## [0.0.5] - 2021-08-13

### General
- Updated to the newest OpenTelemetry Java Instrumentation [v1.1.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.1.0) and OpenTelemetry Java SDK [v1.1.0](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v1.1.0).

### Enhancements
- Configured the OTEL wrapper example to disable not needed resource providers.
- Configured disabled resource providers to match OTEL format (`ResourceProvider` instead of `Resource`).
- Added support for SQS Event wrapper.
- Changed auth token property name to `SPLUNK_ACCESS_TOKEN`.
- Changed the name of Splunk's exporter to `jaeger-thrift-splunk`.

-----
## [0.0.4] - 2021-03-04

### General
- Updated to the newest OpenTelemetry Java Instrumentation [v0.17.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v0.17.0) and OpenTelemetry Java SDK [v0.17.1](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.17.1).

-----
## [0.0.3] - 2021-01-28

### General
- Updated README.md with current env var names
- Updated examples with current env var names
- Upstream OpenTelemetry Java Instrumentation 0.14.0 contains an issue that prevents HTTP context propagation. Therefore, only XRay context propagation works currently.

### Enhancements
- "always on" sampler configured - all spans will be always exported
- Removed HTTP interceptor logging

------
## [0.0.2] - 2021-01-26

### General
- Updated to the newest OpenTelemetry Java Instrumentation [v0.14.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v0.14.0) and OpenTelemetry Java SDK [v0.14.1](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.14.1).
- Improved and extended README.md.

### Enhancements
- Ability to configure wrapper max ingest wait timeout with `OTEL_INSTRUMENTATION_AWS_LAMBDA_FLUSH_TIMEOUT`.
- Additional logging for `jaeger-thrift` exporter controlled by OTEL library log level variable `OTEL_LIB_LOG_LEVEL`.
- Added support for additional W3CBaggagePropagator (`baggage`).

------
## [0.0.1] - 2020-12-28

### General
- First release of the wrapper. 
- Configures Splunk defaults for export and propagation.
- Provides a wide variety of examples.
- Uses OpenTelemetry Java Instrumentation [v0.11.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v0.11.0) and OpenTelemetry Java SDK [v0.11.0](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.11.0).

### Enhancements
- Provides propagator configuration via env props.
- Provides exporter configuration via env props.
- Supports direct Splunk cloud ingest via Jaeger / Thrift over http exporter.