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

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.AwsXrayPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.extension.trace.propagation.OtTracerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PropagatorsInitializer {

  private static final Logger log = LoggerFactory.getLogger(PropagatorsInitializer.class);
  
  private static final Map<String, TextMapPropagator> TEXTMAP_PROPAGATORS = new HashMap<>();
  static {
          TEXTMAP_PROPAGATORS.put("tracecontext", W3CTraceContextPropagator.getInstance());
          TEXTMAP_PROPAGATORS.put("baggage", W3CBaggagePropagator.getInstance());
          TEXTMAP_PROPAGATORS.put("b3", B3Propagator.getInstance());
          TEXTMAP_PROPAGATORS.put("b3multi", B3Propagator.builder().injectMultipleHeaders().build());
          TEXTMAP_PROPAGATORS.put("jaeger", JaegerPropagator.getInstance());
          TEXTMAP_PROPAGATORS.put("ottracer", OtTracerPropagator.getInstance());
          TEXTMAP_PROPAGATORS.put("xray", AwsXrayPropagator.getInstance());
  }

  static void initializePropagators(OpenTelemetrySdkBuilder openTelemetrySdkBuilder, List<String> propagators) {

    log.debug("Configuring propagators: {}", propagators);

    List<TextMapPropagator> textPropagators = new ArrayList<>(propagators.size());
    for (String propagatorId : propagators) {
      TextMapPropagator textPropagator = TEXTMAP_PROPAGATORS.get(propagatorId.trim().toLowerCase());
      if (textPropagator != null) {
        textPropagators.add(textPropagator);
      } else {
        log.error("Propagator {} not found, will not be added.", propagatorId);
      }
    }
    ContextPropagators contextPropagators = ContextPropagators.noop();
    if (textPropagators.size() > 1) {
      contextPropagators = ContextPropagators.create(TextMapPropagator.composite(textPropagators));
    } else if (textPropagators.size() == 1) {
      contextPropagators = ContextPropagators.create(textPropagators.get(0));
    }

    openTelemetrySdkBuilder.setPropagators(contextPropagators);
  }
}
