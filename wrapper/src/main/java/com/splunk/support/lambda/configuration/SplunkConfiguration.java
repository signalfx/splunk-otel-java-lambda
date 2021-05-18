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

import static com.splunk.support.lambda.configuration.Config.getValueOrDefault;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplunkConfiguration {

  private static final Logger log = LoggerFactory.getLogger(SplunkConfiguration.class);

  static final String OTEL_LIB_LOG_LEVEL = "OTEL_LIB_LOG_LEVEL";
  static final String SPLUNK_ACCESS_TOKEN = "splunk.access.token";

  public static void configure() {
    ConfigValidator.validate();
    DefaultConfiguration.applyDefaults();
    configureOtelLogging();
    addSplunkAccessTokenToOtlpHeadersIfNeeded();
  }

  private static void addSplunkAccessTokenToOtlpHeadersIfNeeded() {
    String accessToken = getValueOrDefault(SPLUNK_ACCESS_TOKEN);
    String tracesExporter = getValueOrDefault("otel.traces.exporter");

    if ("otlp".equals(tracesExporter) && !accessToken.isEmpty()) {
      String userOtlpHeaders = getValueOrDefault("otel.exporter.otlp.headers");
      String otlpHeaders =
          (userOtlpHeaders.isEmpty() ? "" : userOtlpHeaders + ",") + "X-SF-TOKEN=" + accessToken;
      System.setProperty("otel.exporter.otlp.headers", otlpHeaders);
    }
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
}
