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

import com.google.auto.service.AutoService;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.autoconfigure.spi.SdkTracerProviderConfigurer;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(SdkTracerProviderConfigurer.class)
public class TracerProviderConfigurer implements SdkTracerProviderConfigurer {

  private static final String OTEL_LAMBDA_LOG_LEVEL = "OTEL_LAMBDA_LOG_LEVEL";

  private static final Logger logger = LoggerFactory.getLogger(TracerProviderConfigurer.class);

  @Override
  public void configure(SdkTracerProviderBuilder sdkTracerProviderBuilder) {

    if (!"DEBUG".equalsIgnoreCase(Config.getValue(OTEL_LAMBDA_LOG_LEVEL))) {
      return;
    }

    maybeEnableLoggingExporter(sdkTracerProviderBuilder);
  }

  private static void maybeEnableLoggingExporter(SdkTracerProviderBuilder builder) {
    // don't install another instance if the user has already explicitly requested it.
    if (loggingExporterIsNotAlreadyConfigured()) {
      builder.addSpanProcessor(SimpleSpanProcessor.create(new LoggingSpanExporter()));
      logger.info("Added logging exporter for debug purposes.");
    }
  }

  private static boolean loggingExporterIsNotAlreadyConfigured() {
    return !"logging".equalsIgnoreCase(Config.getValue("OTEL_TRACES_EXPORTER"));
  }
}
