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

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConfiguratorTest {

  @BeforeAll
  public static void setUp() {
    System.setProperty("otel.exporter", "logging");
    Configurator.configure();
  }

  @Test
  public void shouldSetPropagators() {
    // given

    // when
    // then
    assertThat(GlobalOpenTelemetry.get()).isNotNull();
    OpenTelemetry got = GlobalOpenTelemetry.get();
    assertThat(got.getPropagators().getTextMapPropagator().fields())
        .containsExactlyElementsOf(B3Propagator.getInstance().fields());
  }

  @Test
  public void shouldExportSpansWithoutException() throws InterruptedException {

    // given
    Tracer tracer = GlobalOpenTelemetry.getTracer("testTracer");

    // when
    Span span = tracer.spanBuilder("root").setSpanKind(Span.Kind.SERVER).startSpan();
    try (Scope ignored = Context.current().with(span).makeCurrent()) {
      Thread.sleep(1000);
    } finally {
      span.end();
      OpenTelemetrySdk.getGlobalTracerManagement().forceFlush().join(30, TimeUnit.SECONDS);
    }

    // then - no exception
  }
}
