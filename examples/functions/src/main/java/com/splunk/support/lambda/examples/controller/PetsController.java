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

package com.splunk.support.lambda.examples.controller;

import static com.splunk.support.lambda.examples.model.PetService.generatePet;

import com.splunk.support.lambda.examples.model.Pet;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/pets")
public class PetsController {

  private static final Logger log = LoggerFactory.getLogger(PetsController.class);

  @Inject HttpServletRequest request;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Pet[] listPets(@QueryParam("limit") int limit) {

    if (limit < 1) {
      limit = 10;
    }

    Pet[] result = new Pet[limit];
    for (int i = 0; i < limit; i++) {
      result[i] = generatePet();
      // mock internal call - for context propagation demo purposes
      callPet(result[i].getId());
    }
    return result;
  }

  private void callPet(String id) {
    String callUrl = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
    call(callUrl + "/Prod/pets/" + id);
  }

  private void call(final String address) {

    try {
      URL obj = new URL(address);
      HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();
      urlConnection.setRequestMethod("GET");
      log.info("Executed call to: {}. Result code: {}", address, urlConnection.getResponseCode());
    } catch (Exception e) {
      log.error("Error calling: " + address, e);
    }
  }

  @Path("/{petId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Pet getById() {
    return generatePet();
  }
}
