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

import static com.splunk.support.lambda.ExportersInitializer.initializeExporters;
import static com.splunk.support.lambda.PropagatorsInitializer.initializePropagators;

import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.instrumentation.api.config.ConfigBuilder;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import javax.naming.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configurator {

    private static final Logger log = LoggerFactory.getLogger(Configurator.class);

    private static final String EXPORTERS_CONFIG = "otel.exporter";
    private static final String PROPAGATORS_CONFIG = "otel.propagators";

    static final String OTEL_LIB_LOG_LEVEL = "OTEL_LIB_LOG_LEVEL";

    // Disable all non-lambda AWS resources to make startup faster
    private static final String DISABLED_RESOURCE_PROVIDERS =
            String.join(",",
                    "io.opentelemetry.sdk.extension.resources.OsResource",
                    "io.opentelemetry.sdk.extension.resources.ProcessResource",
                    "io.opentelemetry.sdk.extension.aws.resource.BeanstalkResource",
                    "io.opentelemetry.sdk.extension.aws.resource.Ec2Resource",
                    "io.opentelemetry.sdk.extension.aws.resource.EcsResource",
                    "io.opentelemetry.sdk.extension.aws.resource.EksResource");

    public static void configure() {
        setDefaultValues();
        Config.internalInitializeConfig(new ConfigBuilder()
                .readEnvironmentVariables()
                .readSystemProperties()
                .build());

        configureOtelLogging();

        OpenTelemetrySdkBuilder sdkBuilder = OpenTelemetrySdk.builder();
        initializeExporters(sdkBuilder, Config.get().getListProperty(EXPORTERS_CONFIG), Config.get().asJavaProperties());
        initializePropagators(sdkBuilder, Config.get().getListProperty(PROPAGATORS_CONFIG));
        sdkBuilder.buildAndRegisterGlobal();
    }

    private static void configureOtelLogging() {
        // otel logging - java!
        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);
        consoleHandler.setFormatter(new SimpleFormatter());

        final java.util.logging.Logger otel = java.util.logging.Logger.getLogger("io.opentelemetry");
        otel.setLevel(getOtelLibLogLevel());
        otel.addHandler(consoleHandler);

        log.info("Configured OTEL lib log level: {}", otel.getLevel());
    }

    private static Level getOtelLibLogLevel() {
        String level = System.getenv(OTEL_LIB_LOG_LEVEL);
        if (level != null) {
            try {
                return Level.parse(level);
            } catch (IllegalArgumentException iae) {
                log.debug("Could not parse OTEL lib log level", iae);
            }
        }
        return Level.WARNING;
    }

    private static void setDefaultValues() {
        setDefaultValue("otel.propagators", "b3");
        setDefaultValue("otel.exporter", "jaeger-thrift");
        setDefaultValue("otel.exporter.jaeger.endpoint", "http://localhost:9080/v1/trace");
        setDefaultValue("otel.exporter.jaeger.service.name", "OtelInstrumentedLambda");
        setDefaultValue("otel.java.disabled.resource_providers", DISABLED_RESOURCE_PROVIDERS);
    }

    static void setDefaultValue(String name, String value) {
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

