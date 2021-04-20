# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Updated to the newest OpenTelemetry Java Instrumentation [v1.1.0](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/tag/v1.1.0) and OpenTelemetry Java SDK [v1.1.0](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v1.1.0)
- Configured OTEL wrapper example to disable not needed resource providers

### Changed

- Configured disabled resource providers to match OTEL format (`ResourceProvider` instead of `Resource`)