package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Book;
import org.jboss.as.quickstarts.kitchensink.model.Library;

import javax.ws.rs.core.Response;

/**
 * Created by Filip on 03.12.2016.
 */
public interface LibraryBookRegistration {
    Response registerBook(Library library, Book book);
}
