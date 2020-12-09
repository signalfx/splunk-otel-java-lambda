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

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.DefaultContextPropagators;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.TracerSdkManagement;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.lang.reflect.Method;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configures:
 * - B3 inbound context propagation
 * - logging exporter
 * - debug / finest logging
 *
 */
public final class LambdaConfiguration {

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
        OpenTelemetry.set(
                OpenTelemetrySdk.builder()
                        .setResource(OpenTelemetrySdk.get().getResource())
                        .setClock(OpenTelemetrySdk.get().getClock())
                        .setMeterProvider(OpenTelemetry.getGlobalMeterProvider())
                        .setTracerProvider(unobfuscate(OpenTelemetry.getGlobalTracerProvider()))
                        .setPropagators(DefaultContextPropagators.builder()
                                .addTextMapPropagator(B3Propagator.getInstance())
                                .build())
                        .build());
        // exporter
        TracerSdkManagement tracerSdkManagement = OpenTelemetrySdk.getGlobalTracerManagement();
        tracerSdkManagement.addSpanProcessor(
                SimpleSpanProcessor.builder(new LoggingSpanExporter()).build());
    }

    private static TracerProvider unobfuscate(TracerProvider tracerProvider) {
        if (tracerProvider.getClass().getName().endsWith("TracerSdkProvider")) {
            return tracerProvider;
        }
        try {
            Method unobfuscate = tracerProvider.getClass().getDeclaredMethod("unobfuscate");
            unobfuscate.setAccessible(true);
            return (TracerProvider) unobfuscate.invoke(tracerProvider);
        } catch (Throwable t) {
            return tracerProvider;
        }
    }
}
