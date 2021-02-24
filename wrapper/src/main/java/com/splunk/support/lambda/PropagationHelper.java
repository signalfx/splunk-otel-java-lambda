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

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import java.util.HashMap;
import java.util.Map;

public final class PropagationHelper {

  private PropagationHelper() {}

  private static final TextMapSetter<Map<String, String>> SETTER = new MapSetter();

  public static Map<String, String> createHeaders() {

    TextMapPropagator propagator = GlobalOpenTelemetry.getPropagators().getTextMapPropagator();
    Map<String, String> result = new HashMap<>();
    propagator.inject(Context.current(), result, SETTER);
    return result;
  }

  private static class MapSetter implements TextMapSetter<Map<String, String>> {

    @Override
    public void set(Map<String, String> map, String key, String value) {
      map.put(key, value);
    }
  }
}
