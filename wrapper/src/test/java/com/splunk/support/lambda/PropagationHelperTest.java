package com.splunk.support.lambda;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PropagationHelperTest {

    private void initContextWith(String traceId, String spanId) {
        Map<String, String> inbound = new HashMap<>();
        inbound.put("X-B3-TraceId", traceId);
        inbound.put("X-B3-SpanId", spanId);
        inbound.put("X-B3-Sampled", "1");
        Context extracted = B3Propagator.getInstance().extract(Context.current(), inbound, new TextMapPropagator.Getter<Map<String, String>>() {
            @Override
            public Iterable<String> keys(Map<String, String> stringStringMap) {
                return stringStringMap.keySet();
            }

            @Override
            public String get(Map<String, String> stringStringMap, String s) {
                return stringStringMap.get(s);
            }
        });
        extracted.makeCurrent();
    }

    @Test
    public void shouldCreateB3OutboundHeaders() {

        // given
        OpenTelemetrySdkBuilder openTelemetrySdkBuilder = OpenTelemetrySdk.builder();
        PropagatorsInitializer.initializePropagators(openTelemetrySdkBuilder, Collections.singletonList("b3multi"));
        openTelemetrySdkBuilder.buildAndRegisterGlobal();
        initContextWith("4fd0b6131f19f39af59518d127b0cafe", "0000000000000123");

        // when
        Map<String, String> headers = PropagationHelper.createHeaders();
        // then
        assertThat(headers).hasSize(3);
        assertThat(headers).containsEntry("X-B3-TraceId", "4fd0b6131f19f39af59518d127b0cafe");
        assertThat(headers).containsEntry("X-B3-SpanId", "0000000000000123");
        assertThat(headers).containsEntry("X-B3-Sampled", "1");
    }
}