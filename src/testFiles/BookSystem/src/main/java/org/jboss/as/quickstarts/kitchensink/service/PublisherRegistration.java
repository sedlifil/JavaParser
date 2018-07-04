package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Publisher;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Filip on 21.12.2016.
 */
public interface PublisherRegistration {
    Response register(Publisher publisher);

    List<Publisher> getPublishers();

    Publisher findPublisher(String id);
}
