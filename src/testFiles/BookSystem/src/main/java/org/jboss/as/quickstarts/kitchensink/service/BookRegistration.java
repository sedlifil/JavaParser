package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Book;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Filip on 21.12.2016.
 */
public interface BookRegistration {
    Response register(Book book);

    List<Book> getBooks();

    Book findBook(String id);
}
