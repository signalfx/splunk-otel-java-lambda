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

import static com.splunk.support.lambda.configuration.Config.setDefaultValue;
import static com.splunk.support.lambda.configuration.Names.OTEL_LIB_LOG_LEVEL;
import static com.splunk.support.lambda.configuration.Names.OTEL_TRACES_EXPORTER;

public class DefaultConfiguration {

  private static final String DISABLED_RESOURCE_PROVIDERS =
      String.join(
          ",",
          "io.opentelemetry.sdk.extension.resources.OsResourceProvider",
          "io.opentelemetry.sdk.extension.resources.ProcessResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.BeanstalkResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.Ec2ResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.EcsResourceProvider",
          "io.opentelemetry.sdk.extension.aws.resource.EksResourceProvider");

  static void applyDefaults() {

    // by default no metrics are exported
    setDefaultValue("otel.metrics.exporter", "none");

    setDefaultValue("otel.propagators", "tracecontext,baggage");
    setDefaultValue(OTEL_TRACES_EXPORTER, "otlp");

    // sample ALL
    setDefaultValue("otel.traces.sampler", "always_on");

    // disable non-lambda resource providers
    setDefaultValue("otel.java.disabled.resource.providers", DISABLED_RESOURCE_PROVIDERS);

    // default info logging for otel libs
    setDefaultValue(OTEL_LIB_LOG_LEVEL, "INFO");
  }
}
