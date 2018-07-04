
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
import javax.ws.rs.core.Response;

import org.jboss.as.quickstarts.kitchensink.block.Block;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.model.Book;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;
import org.jboss.as.quickstarts.kitchensink.service.AuthorRegistration;
import org.jboss.as.quickstarts.kitchensink.service.BookRegistration;
import org.jboss.as.quickstarts.kitchensink.service.PublisherRegistration;

/**
 * JAX-RS Example
 * <p>
 * This class produces a RESTful service to read the contents of the members table.
 */
@Path("/books")
@RequestScoped
public class BookRegistrationRESTController {
    @Inject
    private BookRegistration bookRegistration;

    @Inject
    private AuthorRegistration authorRegistration;

    @Inject
    private PublisherRegistration publisherRegistration;

    @GET
    public JsonArray findAll() {
        JsonArrayBuilder list = Json.createArrayBuilder();
        List<Book> all = bookRegistration.getBooks();
        all.forEach(m -> list.add(m.toJson()));
        return list.build();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public JsonObject findById(@PathParam("id") Long id) {
         Book book = bookRegistration.findBook(id.toString());
        return book.toJson();
    }

    @POST
    @Path("/register/{idAuthor:[0-9][0-9]*}/{idPublisher:[0-9][0-9]*}")
    public Response save(@Valid Book book, @PathParam("idAuthor") Long idAuthor, @PathParam("idPublisher") Long idPublisher) {
        Author findAuthor = authorRegistration.findAuthor(Long.toString(idAuthor));
        Publisher findPublisher = publisherRegistration.findPublisher(Long.toString(idPublisher));

        System.out.println(findPublisher.getAuthors());
        book.setPublisher(findPublisher);
        book.setAuthors(findAuthor);
        return bookRegistration.register(book);
    }
}
