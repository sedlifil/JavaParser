package org.jboss.as.quickstarts.kitchensink.serviceImpl;



import org.jboss.as.quickstarts.kitchensink.dao.*;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;
import org.jboss.as.quickstarts.kitchensink.service.AuthorContractPublisher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by Filip on 04.12.2016.
 */
@Stateless
public class AuthorContractPublisherImpl implements AuthorContractPublisher {


    @Inject
    private AuthorDAO authorDAO;

    @Inject
    private Logger logger;

    @Override
    public Response register(Author author, Publisher publisher) {
        String s;
        if(author.getPublishers().stream().filter(y -> y.getId() == publisher.getId()).count() == 1){
            s = "Autor: " + author.getSurname() + " " + author.getName() +
                    "už má uzavřenou smlouvu s " + publisher.getName();
            logger.info(s);
            return Response.status(513).build();
        }

        author.getPublishers().add(publisher);
        try {
            authorDAO.update(author);
            s = "Autor: " + author.getSurname() + " " + author.getName() +
                    "uzavřel smlouvu s " + publisher.getName();
            logger.info(s);
            return Response.ok().build();
        } catch (Exception e) {
            s = "Error s uzavřením smlouvy!" ;
            logger.info(s);
            return Response.status(514).build();
        }



    }
}
