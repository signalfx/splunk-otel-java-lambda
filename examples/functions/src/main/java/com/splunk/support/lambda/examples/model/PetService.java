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

package com.splunk.support.lambda.examples.model;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PetService {
  private static final String[] BREEDS =
      new String[] {"Terrier", "Jack Russell", "Bloodhound", "Dalmatian", "German Shepherd"};

  private static final String[] NAMES =
      new String[] {
        "Rocky", "Cody", "Ginger", "Roxy", "Bear", "Gracie", "Tucker", "Coco", "Murphy", "Oscar",
        "Kajtek"
      };

  public static String generateBreed() {
    return BREEDS[ThreadLocalRandom.current().nextInt(0, BREEDS.length - 1)];
  }

  public static String generateName() {
    return NAMES[ThreadLocalRandom.current().nextInt(0, NAMES.length - 1)];
  }

  public static Pet generatePet() {
    return new Pet(
        UUID.randomUUID().toString(), PetService.generateBreed(), PetService.generateName());
  }
}
