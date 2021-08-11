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

package io.opentelemetry.instrumentation.awslambda.v1_0;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.OpenTelemetrySdkAutoConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Class will be donated to OpenTelemetry Java Instrumentation project. */
public class TracingSqsEventWrapper extends TracingSqsEventHandler {

  private final WrappedLambda wrappedLambda;

  public TracingSqsEventWrapper() {
    this(OpenTelemetrySdkAutoConfiguration.initialize(), WrappedLambda.fromConfiguration());
  }

  // Visible for testing
  TracingSqsEventWrapper(OpenTelemetrySdk openTelemetrySdk, WrappedLambda wrappedLambda) {
    super(openTelemetrySdk, WrapperConfiguration.flushTimeout());
    this.wrappedLambda = wrappedLambda;
  }

  private Object[] createParametersArray(Method targetMethod, SQSEvent input, Context context) {
    Class<?>[] parameterTypes = targetMethod.getParameterTypes();
    Object[] parameters = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      // loop through to populate each index of parameter
      Object parameter = null;
      Class clazz = parameterTypes[i];
      boolean isContext = clazz.equals(Context.class);
      if (i == 0 && !isContext) {
        // first position if it's not context
        parameter = input;
      } else if (isContext) {
        // populate context
        parameter = context;
      }
      parameters[i] = parameter;
    }
    return parameters;
  }

  @Override
  protected void handleEvent(SQSEvent sqsEvent, Context context) {
    Method targetMethod = wrappedLambda.getRequestTargetMethod();
    Object[] parameters = createParametersArray(targetMethod, sqsEvent, context);

    try {
      targetMethod.invoke(wrappedLambda.getTargetObject(), parameters);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Method is inaccessible", e);
    } catch (InvocationTargetException e) {
      throw (e.getCause() instanceof RuntimeException
          ? (RuntimeException) e.getCause()
          : new RuntimeException(e.getTargetException()));
    }
  }
}
