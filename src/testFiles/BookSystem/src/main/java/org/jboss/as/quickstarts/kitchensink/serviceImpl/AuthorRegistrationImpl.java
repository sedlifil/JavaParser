package org.jboss.as.quickstarts.kitchensink.serviceImpl;



import org.jboss.as.quickstarts.kitchensink.dao.*;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.service.AuthorRegistration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Filip on 22.12.2016.
 */
@Stateless
public class AuthorRegistrationImpl implements AuthorRegistration {
    @Inject
    private AuthorDAO authorDAO;

    @Inject
    private Logger logger;


    @Override
    public Response register(Author author) {
        String s;
        try {
            authorDAO.save(author);
            s = "Vytvoření autora: " + author.getSurname() + " " + author.getName();
            logger.info(s);
            return Response.ok().build();
        } catch (Exception e) {
            s = "Error s ukladaním: " + author.getSurname() + " " + author.getName();
            logger.info(s);
            return Response.status(512).build();
        }
    }

    @Override
    public List<Author> getAuthors() {
        return authorDAO.list();
    }



    @Override
    public Author findAuthor(String id) {
        return authorDAO.find(Long.parseLong(id));
    }
}
