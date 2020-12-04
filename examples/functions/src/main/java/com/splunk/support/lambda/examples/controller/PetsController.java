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

import com.splunk.support.lambda.examples.model.Pet;
import com.splunk.support.lambda.examples.model.PetData;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Path("/pets")
public class PetsController {

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
            // mock call
            String callUrl = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));

            call(callUrl+"/Prod/pets/"+newPet.getId());

        }
        return outputPets;
    }

    private void call(final String address) {

        try {
            URL obj = new URL(address);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            System.out.println("Calling: "+address+". Got="+con.getResponseCode());
        } catch (Exception e) {
            System.err.println("Calling: "+address+". ERROR="+e);
        }
    }

    @Path("/{petId}") @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Pet listPets() {
        Pet newPet = new Pet();
        newPet.setId(UUID.randomUUID().toString());
        newPet.setBreed(PetData.getRandomBreed());
        newPet.setDateOfBirth(PetData.getRandomDoB());
        newPet.setName(PetData.getRandomName());
        return newPet;
    }
}
