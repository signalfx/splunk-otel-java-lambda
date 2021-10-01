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

import static com.splunk.support.lambda.configuration.SplunkConfiguration.SPLUNK_ACCESS_TOKEN;
import static io.opentelemetry.api.internal.StringUtils.isNullOrEmpty;

import com.google.auto.service.AutoService;
import io.jaegertracing.thrift.internal.senders.HttpSender;
import io.opentelemetry.exporter.jaeger.thrift.JaegerThriftSpanExporter;
import io.opentelemetry.exporter.jaeger.thrift.JaegerThriftSpanExporterBuilder;
import io.opentelemetry.sdk.autoconfigure.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurableSpanExporterProvider;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(ConfigurableSpanExporterProvider.class)
public class JaegerThriftSpanExporterFactory implements ConfigurableSpanExporterProvider {
  private static final Logger log = LoggerFactory.getLogger(JaegerThriftSpanExporterFactory.class);

  public static final String OTEL_EXPORTER_JAEGER_ENDPOINT = "otel.exporter.jaeger.endpoint";

  // http://localhost:9080/v1/trace is the default endpoint for SmartAgent
  // http://localhost:14268/api/traces is the default endpoint for otel-collector
  private static final String OTEL_EXPORTER_JAEGER_ENDPOINT_DEFAULT =
      "http://127.0.0.1:9080/v1/trace";

  @Override
  public SpanExporter createExporter(ConfigProperties config) {
    JaegerThriftSpanExporterBuilder builder = JaegerThriftSpanExporter.builder();

    String endpoint = config.getString(OTEL_EXPORTER_JAEGER_ENDPOINT);
    if (isNullOrEmpty(endpoint)) {
      endpoint = OTEL_EXPORTER_JAEGER_ENDPOINT_DEFAULT;
      log.debug(
          "Using default value for OTEL_EXPORTER_JAEGER_ENDPOINT="
              + OTEL_EXPORTER_JAEGER_ENDPOINT_DEFAULT);
    }
    String token = config.getString(SPLUNK_ACCESS_TOKEN);
    if (isNullOrEmpty(token)) {
      log.debug("Using jaeger-thrift exporter without authentication");
      builder.setEndpoint(endpoint);
    } else {
      log.debug("Using authenticated jaeger-thrift exporter");
      builder.setThriftSender(
          new HttpSender.Builder(endpoint)
              .withClient(
                  new OkHttpClient.Builder()
                      .addInterceptor(new AuthTokenInterceptor(token))
                      .build())
              .build());
    }
    return builder.build();
  }

  @Override
  public String getName() {
    return "jaeger-thrift-splunk";
  }
}
