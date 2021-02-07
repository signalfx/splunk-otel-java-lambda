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

package com.splunk.support.lambda;

import io.jaegertracing.thrift.internal.senders.HttpSender;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.exporter.jaeger.thrift.JaegerThriftSpanExporter;
import io.opentelemetry.exporter.jaeger.thrift.JaegerThriftSpanExporterBuilder;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.config.TraceConfig;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportersInitializer {

  private static final Logger log = LoggerFactory.getLogger(ExportersInitializer.class);

  static SdkTracerProvider configureExporters(List<String> exporters, Properties config) {

    log.debug("Installing exporters: {}", exporters);

    List<SpanProcessor> spanProcessors = new ArrayList<>();
    for (String exporterName : exporters) {
      SpanProcessor exporter = createExporter(exporterName, config);
      if (exporter != null) {
        spanProcessors.add(exporter);
      }
    }
    TraceConfig traceConfig = TraceConfig.builder().setSampler(Sampler.alwaysOn()).build();

    SdkTracerProvider result =
        SdkTracerProvider.builder()
            .addSpanProcessor(SpanProcessor.composite(spanProcessors))
            .setTraceConfig(traceConfig)
            .build();
    return result;
  }

  private static SpanProcessor createExporter(String exporterName, Properties config) {

    SpanExporter spanExporter = getSpanExporter(exporterName, config);
    if (spanExporter != null) {
      return SimpleSpanProcessor.builder(spanExporter).setExportOnlySampled(false).build();
    }
    log.warn("Exporter: {} not found", exporterName);
    return null;
  }

  private static SpanExporter getSpanExporter(String exporterName, Properties config) {

    switch (exporterName) {
      case "zipkin":
        return zipkinSpanExporter(config);
      case "otlp":
        return otlpGrpcSpanExporter(config);
      case "logging":
        return loggingSpanExporter(config);
      case "jaeger":
        return jaegerGrpcSpanExporter(config);
      case "jaeger-thrift":
        return jaegerThriftSpanExporter(config);
    }
    return null;
  }

  private static SpanExporter jaegerGrpcSpanExporter(Properties config) {
    return JaegerGrpcSpanExporter.builder().readProperties(config).build();
  }

  private static SpanExporter jaegerThriftSpanExporter(Properties config) {
    JaegerThriftSpanExporterBuilder builder =
        JaegerThriftSpanExporter.builder().readProperties(config);
    OkHttpClient client =
        new OkHttpClient.Builder().addInterceptor(new AuthTokenInterceptor()).build();
    HttpSender thriftSender =
        new HttpSender.Builder(config.getProperty("otel.exporter.jaeger.endpoint"))
            .withClient(client)
            .build();
    builder.setThriftSender(thriftSender);
    return builder.build();
  }

  private static SpanExporter loggingSpanExporter(Properties config) {
    return new LoggingSpanExporter();
  }

  private static SpanExporter otlpGrpcSpanExporter(Properties config) {
    return OtlpGrpcSpanExporter.builder().readProperties(config).build();
  }

  private static SpanExporter zipkinSpanExporter(Properties config) {
    return ZipkinSpanExporter.builder().readProperties(config).build();
  }
}
