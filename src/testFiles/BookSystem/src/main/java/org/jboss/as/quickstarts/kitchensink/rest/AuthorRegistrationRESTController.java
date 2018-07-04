package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.as.quickstarts.kitchensink.block.Block;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.service.AuthorRegistration;

/**
 * JAX-RS Example
 * <p>
 * This class produces a RESTful service to read the contents of the members table.
 */
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/authors")
@RequestScoped
/**
 * Created by filip on 02.11.17.
 */
@Block("key1")
public class AuthorRegistrationRESTController {
    @Inject
    private AuthorRegistration authorRegistration;

    @Block("key1")
    @GET
    public JsonArray findAll() {
        JsonArrayBuilder list = Json.createArrayBuilder();
        List<Author> all = authorRegistration.getAuthors();
        all.forEach(m -> list.add(m.toJson()));
        return list.build();
    }

    @Block("key2")
    @GET
    @Path("/{id:[0-9][0-9]*}")
    public JsonObject findById(@PathParam("id") Long id) {
        List<Author> all = authorRegistration.getAuthors();
        all =  all.stream().filter(y -> y.getId() == id).collect(Collectors.toList());
        if (all.size() == 1) {
            return all.get(0).toJson();
        }
        return all.get(0).toJson();
    }

    @POST
    public Response save(@Valid Author author) {
        authorRegistration.register(author);
        return Response.ok().build();
    }

}


