package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Author;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Filip on 21.12.2016.
 */
public interface AuthorRegistration {
    Response register(Author author);

    List<Author> getAuthors();

    Author findAuthor(String id);
}
