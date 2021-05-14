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

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Config {

  private static final Logger log = LoggerFactory.getLogger(Config.class);

  static void setDefaultValue(String name, String value) {
    if (!isConfigured(name)) {
      log.info("Setting default value. name={}, value={}", name, value);
      System.setProperty(name, value);
    }
  }

  private static boolean isConfigured(String name) {
    return (System.getProperty(name) != null || System.getenv(toEnvVarName(name)) != null);
  }

  static String getValueOrDefault(String name) {

    String result = System.getProperty(name);
    if (result == null) {
      String envValue = System.getenv(toEnvVarName(name));
      result = (envValue == null ? "" : envValue);
    }
    return result;
  }

  private static final Pattern ENV_REPLACEMENT = Pattern.compile("[^a-zA-Z0-9_]");

  private static String toEnvVarName(String propertyName) {
    return ENV_REPLACEMENT.matcher(propertyName.toUpperCase()).replaceAll("_");
  }
}
