package org.jboss.as.quickstarts.kitchensink.serviceImpl;

/**
 * Created by Filip on 21.12.2016.
 */


import org.jboss.as.quickstarts.kitchensink.dao.PublisherDAO;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;
import org.jboss.as.quickstarts.kitchensink.service.PublisherRegistration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class PublisherRegistrationImpl implements PublisherRegistration {

    @Inject
    private PublisherDAO publisherDAO;

    @Inject
    private Logger logger;

    @Override
    public Response register(Publisher publisher) {
        String s;
        try {
            publisherDAO.save(publisher);
            s = "Vytvoření nakladatelství: " + publisher.getName() + " " + publisher.getAddress();
            logger.info(s);
            return Response.ok().build();
        } catch (Exception e) {
            s = "Error s ukladaním: " + publisher.getName();
            logger.info(s);
            return Response.status(512).build();
        }

    }

    @Override
    public List<Publisher> getPublishers() {
        return publisherDAO.list();
    }

    @Override
    public Publisher findPublisher(String id) {
        return publisherDAO.find(Long.parseLong(id));
    }
}
