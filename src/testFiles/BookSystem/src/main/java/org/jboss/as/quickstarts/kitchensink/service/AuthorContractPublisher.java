package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;

import javax.ws.rs.core.Response;

/**
 * Created by Filip on 04.12.2016.
 */
public interface AuthorContractPublisher {

    Response register(Author author, Publisher publisher);
}
