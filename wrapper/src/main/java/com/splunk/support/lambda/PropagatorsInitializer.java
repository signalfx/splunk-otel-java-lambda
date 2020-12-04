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

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.api.trace.propagation.HttpTraceContext;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.DefaultContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.AwsXRayPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.extension.trace.propagation.OtTracerPropagator;
import io.opentelemetry.extension.trace.propagation.TraceMultiPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import java.lang.reflect.Method;
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
          TEXTMAP_PROPAGATORS.put("tracecontext", HttpTraceContext.getInstance());
          TEXTMAP_PROPAGATORS.put("b3", B3Propagator.getInstance());
          TEXTMAP_PROPAGATORS.put("b3multi", B3Propagator.builder().injectMultipleHeaders().build());
          TEXTMAP_PROPAGATORS.put("jaeger", JaegerPropagator.getInstance());
          TEXTMAP_PROPAGATORS.put("ottracer", OtTracerPropagator.getInstance());
          TEXTMAP_PROPAGATORS.put("xray", AwsXRayPropagator.getInstance());
  }

  static void initializePropagators(List<String> propagators) {

    log.debug("Configuring propagators: "+propagators);

    DefaultContextPropagators.Builder propagatorsBuilder = DefaultContextPropagators.builder();
    List<TextMapPropagator> textPropagators = new ArrayList<>(propagators.size());
    for (String propagatorId : propagators) {
      TextMapPropagator textPropagator = TEXTMAP_PROPAGATORS.get(propagatorId.trim().toLowerCase());
      if (textPropagator != null) {
        textPropagators.add(textPropagator);
      }
    }
    if (textPropagators.size() > 1) {
      TraceMultiPropagator.Builder traceMultiPropagatorBuilder = TraceMultiPropagator.builder();
      for (TextMapPropagator textPropagator : textPropagators) {
        traceMultiPropagatorBuilder.addPropagator(textPropagator);
      }
      propagatorsBuilder.addTextMapPropagator(traceMultiPropagatorBuilder.build());
    } else if (textPropagators.size() == 1) {
      propagatorsBuilder.addTextMapPropagator(textPropagators.get(0));
    }
    // Register it in the global propagators:
    setGlobalPropagators(propagatorsBuilder.build());
  }

  public static void setGlobalPropagators(ContextPropagators propagators) {
    OpenTelemetry.set(
            OpenTelemetrySdk.builder()
                    .setResource(OpenTelemetrySdk.get().getResource())
                    .setClock(OpenTelemetrySdk.get().getClock())
                    .setMeterProvider(OpenTelemetry.getGlobalMeterProvider())
                    .setTracerProvider(unobfuscate(OpenTelemetry.getGlobalTracerProvider()))
                    .setPropagators(propagators)
                    .build());
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
