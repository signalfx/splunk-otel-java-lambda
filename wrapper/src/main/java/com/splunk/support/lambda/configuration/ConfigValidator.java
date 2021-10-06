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

import static com.splunk.support.lambda.configuration.Config.getValue;

import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigValidator {

  private static final Logger log = LoggerFactory.getLogger(ConfigValidator.class);

  static void validate() {
    String resourceAttributes = getValue("otel.resource.attributes");
    if (!resourceAttributes.contains(ResourceAttributes.SERVICE_NAME.getKey())) {
      log.warn(
          "Resource attribute 'service.name' is not set: your service is unnamed and will be difficult to identify."
              + " Please Set your service name using the 'OTEL_RESOURCE_ATTRIBUTES' environment variable"
              + " or the 'otel.resource.attributes' system property."
              + " E.g. 'export OTEL_RESOURCE_ATTRIBUTES=\"service.name=<YOUR_SERVICE_NAME_HERE>\"'");
    }
  }
}
