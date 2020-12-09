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
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.List;
import java.util.Properties;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportersInitializer {

    private static final Logger log = LoggerFactory.getLogger(Configurator.class);

    static synchronized void initializeExporters(List<String> exporters, Properties config) {

        log.debug("Installing exporters: {}", exporters);
        for (String exporterName : exporters) {
            installExporter(exporterName, config, getSpanExporter(exporterName, config));
        }
    }

    private static SpanExporter getSpanExporter(String exporterName, Properties config) {

        switch (exporterName) {
            case "zipkin": return zipkinSpanExporter(config);
            case "otlp": return otlpGrpcSpanExporter(config);
            case "logging": return loggingSpanExporter(config);
            case "jaeger": return jaegerGrpcSpanExporter(config);
            case "jaeger-thrift": return jaegerThriftSpanExporter(config);
        }
        return null;
    }

    private static void installExporter(String exporterName, Properties config, SpanExporter spanExporter) {

        if (spanExporter != null) {
            BatchSpanProcessor spanProcessor =
                    BatchSpanProcessor.builder(spanExporter).readProperties(config).build();
            OpenTelemetrySdk.getGlobalTracerManagement().addSpanProcessor(spanProcessor);
            log.debug("Installed span exporter: {}",  exporterName);
        } else {
            log.warn("Exporter: {} not found", exporterName);
        }
    }

    private static SpanExporter jaegerGrpcSpanExporter(Properties config) {
        return JaegerGrpcSpanExporter.builder().readProperties(config).build();
    }

    private static SpanExporter jaegerThriftSpanExporter(Properties config) {
        JaegerThriftSpanExporter.Builder builder = JaegerThriftSpanExporter.builder().readProperties(config);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new AuthTokenInterceptor()).build();
        HttpSender thriftSender = new HttpSender.Builder(config.getProperty("otel.exporter.jaeger.endpoint")).withClient(client).build();
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