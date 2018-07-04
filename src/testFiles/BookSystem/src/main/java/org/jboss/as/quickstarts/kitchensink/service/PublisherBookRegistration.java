package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Book;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;

import javax.ws.rs.core.Response;

/**
 * Created by Filip on 03.12.2016.
 */
public interface PublisherBookRegistration {
    Response registerBook(Publisher publisher, Book book);

    Publisher findPublisher(String id);

    Book findBook(String id);
}
