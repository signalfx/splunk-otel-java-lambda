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

package com.splunk.support.lambda.examples;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** Configures: - B3 inbound context propagation - logging exporter - debug / finest logging */
public final class LambdaConfiguration {

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

    configureOpenTelemetry();
    configureLogging();
  }

  private static void configureLogging() {
    // otel logging - java!
    final ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.FINEST);
    consoleHandler.setFormatter(new SimpleFormatter());

    final Logger otel = Logger.getLogger("io.opentelemetry");
    otel.setLevel(Level.FINEST);
    otel.addHandler(consoleHandler);
  }

  private static void configureOpenTelemetry() {

    // by default no metrics are exported
    System.setProperty("otel.metrics.exporter", "none");

    // jaeger-thrift defaults
    System.setProperty("otel.traces.exporter", "logging");
    System.setProperty("otel.resource.attributes", "service.name=OtelInstrumentedLambda");
    System.setProperty("otel.java.disabled.resource.providers", DISABLED_RESOURCE_PROVIDERS);
  }
}
