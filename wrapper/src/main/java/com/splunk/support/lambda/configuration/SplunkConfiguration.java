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

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.spi.config.PropertySource;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(PropertySource.class)
public class SplunkConfiguration implements PropertySource {

  private static final Logger log = LoggerFactory.getLogger(SplunkConfiguration.class);

  static final String OTEL_LIB_LOG_LEVEL = "OTEL_LIB_LOG_LEVEL";

  private static final String DISABLED_RESOURCE_PROVIDERS =
      String.join(
          ",",
          "io.opentelemetry.sdk.extension.resources.OsResource",
          "io.opentelemetry.sdk.extension.resources.ProcessResource",
          "io.opentelemetry.sdk.extension.aws.resource.BeanstalkResource",
          "io.opentelemetry.sdk.extension.aws.resource.Ec2Resource",
          "io.opentelemetry.sdk.extension.aws.resource.EcsResource",
          "io.opentelemetry.sdk.extension.aws.resource.EksResource");

  public static void configure() {
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

  @Override
  public Map<String, String> getProperties() {
    Map<String, String> config = new HashMap<>();

    // by default no metrics are exported
    config.put("otel.metrics.exporter", "none");

    // jaeger-thrift defaults
    config.put("otel.trace.exporter", "jaeger-thrift-splunk");
    config.put(OTEL_EXPORTER_JAEGER_ENDPOINT, "http://localhost:9080/v1/trace");
    config.put("otel.exporter.jaeger.service.name", "OtelInstrumentedLambda");

    // B3 propagation
    config.put("otel.propagators", "b3");

    // sample ALL
    config.put("otel.trace.sampler", "always_on");

    // disable non-lambda resource providers
    config.put("otel.java.disabled.resource_providers", DISABLED_RESOURCE_PROVIDERS);

    return config;
  }
}
