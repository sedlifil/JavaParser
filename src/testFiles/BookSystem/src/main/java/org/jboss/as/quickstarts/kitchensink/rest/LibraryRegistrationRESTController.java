package org.jboss.as.quickstarts.kitchensink.rest;

import java.util.List;
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
import org.jboss.as.quickstarts.kitchensink.model.Library;
import org.jboss.as.quickstarts.kitchensink.service.LibraryRegistration;

/**
 * JAX-RS Example
 * <p>
 * This class produces a RESTful service to read the contents of the members table.
 */
@Path("/libraries")
@RequestScoped
/**
 * Created by filip on 02.11.17.
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Block("key2")
public class LibraryRegistrationRESTController {

    @Inject
    private LibraryRegistration libraryRegistration;
    @Block({"key2", "key3"})
    @GET
    public JsonArray findAll() {
        JsonArrayBuilder list = Json.createArrayBuilder();
        List<Library> all = libraryRegistration.getLibraries();
        all.forEach( m -> list.add(m.toJson()));
        return list.build();
    }

    @Block({"key1", "key3"})
    @GET
    @Path("/{id:[0-9][0-9]*}")
    public JsonObject findById(@PathParam("id") Long id) {
        Library library = libraryRegistration.findLibrary(id.toString());
        return library.toJson();
    }

    @Block("key1")
    @POST
    public Response save(@Valid Library library) {
        libraryRegistration.register(library);
        return Response.ok().build();
    }
}
