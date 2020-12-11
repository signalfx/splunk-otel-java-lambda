package com.splunk.support.lambda;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import java.util.HashMap;
import java.util.Map;

public final class PropagationHelper {

    private PropagationHelper() {}

    private static final TextMapPropagator.Setter<Map<String, String>> SETTER = new MapSetter();

    public static Map<String, String> createHeaders() {

        TextMapPropagator propagator = OpenTelemetry.getGlobalPropagators().getTextMapPropagator();
        Map<String, String> result = new HashMap<>();
        propagator.inject(Context.current(), result, SETTER);
        return result;
    }

    private static class MapSetter implements TextMapPropagator.Setter<Map<String, String>> {

        @Override
        public void set(Map<String, String> map, String key, String value) {
            map.put(key, value);
        }
    }
}
