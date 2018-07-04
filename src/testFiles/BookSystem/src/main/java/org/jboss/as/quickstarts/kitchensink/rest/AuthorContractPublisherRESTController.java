package org.jboss.as.quickstarts.kitchensink.rest;

import org.jboss.as.quickstarts.kitchensink.block.Block;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;
import org.jboss.as.quickstarts.kitchensink.service.AuthorContractPublisher;
import org.jboss.as.quickstarts.kitchensink.service.AuthorRegistration;
import org.jboss.as.quickstarts.kitchensink.service.PublisherRegistration;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;

/**
 * JAX-RS Example
 * <p>
 * This class produces a RESTful service to read the contents of the members table.
 */
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/authorsContractPublishers")
@RequestScoped
/**
 * Created by filip on 02.11.17.
 */
public class AuthorContractPublisherRESTController {

    @Inject
    private AuthorRegistration authorRegistration;

    @Inject
    private PublisherRegistration publisherRegistration;

    @Inject
    private AuthorContractPublisher authorContractPublisher;

    @Block("key2")
    @POST
    public Response registerAuthorContractPublisher(JsonObject jsonObject) {
        Set<Map.Entry<String, JsonValue>> kvPairs = jsonObject.entrySet();
        int idKeysCount = (int) kvPairs.stream().filter(y -> y.getKey().equals("idAuthor") || y.getKey().equals("idPublisher")).count();

        if(idKeysCount == 2){
            Author author = authorRegistration.findAuthor(jsonObject.getString("idAuthor"));
            Publisher publisher = publisherRegistration.findPublisher(jsonObject.getString("idPublisher"));

            if (author != null && publisher != null){
                return authorContractPublisher.register(author,publisher);
            }
        }
        return Response.status(512).build();
    }
}
