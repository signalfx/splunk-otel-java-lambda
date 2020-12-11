/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.splunk.support.lambda.examples.controller;

import com.splunk.support.lambda.PropagationHelper;
import com.splunk.support.lambda.examples.model.Pet;
import com.splunk.support.lambda.examples.model.PetData;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/animals")
public class AnimalsController {

    private static final Logger log = LoggerFactory.getLogger(PetsController.class);

    @Inject
    HttpServletRequest request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Pet[] listPets(@QueryParam("limit") int limit) {
        if (limit < 1) {
            limit = 10;
        }

        Pet[] outputPets = new Pet[limit];

        for (int i = 0; i < limit; i++) {
            Pet newPet = new Pet();
            newPet.setId(UUID.randomUUID().toString());
            newPet.setName(PetData.getRandomName());
            newPet.setBreed(PetData.getRandomBreed());
            newPet.setDateOfBirth(PetData.getRandomDoB());
            outputPets[i] = newPet;
            // mock internal call
            String callUrl = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
            // just to call a different lambda
            call(callUrl+"/Prod/hello/"+newPet.getId());
        }
        return outputPets;
    }

    private void call(final String address) {

        try {
            URL obj = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("GET");
            addPropagationHeaders(urlConnection);
            log.info("Executed call to: {}. Result code: {}", address, urlConnection.getResponseCode());
        } catch (Exception e) {
            log.error("Error calling: "+address, e);
        }
    }

    private void addPropagationHeaders(HttpURLConnection urlConnection) {
        Map<String, String> propagationHeaders = PropagationHelper.createHeaders();
        for (Map.Entry<String, String> header : propagationHeaders.entrySet()) {
            urlConnection.setRequestProperty(header.getKey(), header.getValue());
        }
    }
}
