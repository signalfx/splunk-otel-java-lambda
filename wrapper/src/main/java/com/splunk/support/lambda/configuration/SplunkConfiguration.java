/*
 * Copyright Splunk Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.splunk.support.lambda.configuration;

import static com.splunk.support.lambda.configuration.JaegerThriftSpanExporterFactory.OTEL_EXPORTER_JAEGER_ENDPOINT;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplunkConfiguration {

  private static final Logger log = LoggerFactory.getLogger(SplunkConfiguration.class);

  static final String OTEL_LIB_LOG_LEVEL = "OTEL_LIB_LOG_LEVEL";

  private static final String DISABLED_RESOURCE_PROVIDERS =
      String.join(
          ",",
          "io.opentelemetry.sdk.extension.resources.OsResourceProvider",
          "io.opentelemetry.sdk.extension.resources.ProcessResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.BeanstalkResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.Ec2ResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.EcsResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.EksResourceProvider");

  public static void configure() {
    setDefaults();
    configureOtelLogging();
  }

  private static void configureOtelLogging() {
    // otel logging - java.util.logging
    Level level = getOtelLibLogLevel();
    final Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(level);
    consoleHandler.setFormatter(new SimpleFormatter());

    final java.util.logging.Logger otel = java.util.logging.Logger.getLogger("io.opentelemetry");
    otel.setLevel(level);
    otel.addHandler(consoleHandler);

    log.info("Configured OTEL library log level: {}", level);
  }

  private static Level getOtelLibLogLevel() {
    String level = System.getenv(OTEL_LIB_LOG_LEVEL);
    if (level != null) {
      try {
        return Level.parse(level);
      } catch (IllegalArgumentException iae) {
        log.debug("Could not parse OTEL library log level", iae);
      }
    }
    return Level.WARNING;
  }

  private static void setDefaults() {
    // by default no metrics are exported
    setDefaultValue("otel.metrics.exporter", "none");

    // jaeger-thrift defaults
    setDefaultValue("otel.traces.exporter", "jaeger-thrift-splunk");
    setDefaultValue(OTEL_EXPORTER_JAEGER_ENDPOINT, "http://localhost:9080/v1/trace");
    setDefaultValue("otel.resource.attributes", "service.name=OtelInstrumentedLambda");

    // B3 propagation
    setDefaultValue("otel.propagators", "b3");

    // sample ALL
    setDefaultValue("otel.traces.sampler", "always_on");

    // disable non-lambda resource providers
    setDefaultValue("otel.java.disabled.resource.providers", DISABLED_RESOURCE_PROVIDERS);
  }

  private static void setDefaultValue(String name, String value) {
    if (!isConfigured(name)) {
      log.info("Setting default value. name={}, value={}", name, value);
      System.setProperty(name, value);
    }
  }

  private static boolean isConfigured(String name) {
    return (System.getProperty(name) != null || System.getenv(toEnvVarName(name)) != null);
  }

  private static final Pattern ENV_REPLACEMENT = Pattern.compile("[^a-zA-Z0-9_]");

  private static String toEnvVarName(String propertyName) {
    return ENV_REPLACEMENT.matcher(propertyName.toUpperCase()).replaceAll("_");
  }
}
