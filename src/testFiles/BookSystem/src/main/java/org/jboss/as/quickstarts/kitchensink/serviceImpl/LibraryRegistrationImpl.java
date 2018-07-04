package org.jboss.as.quickstarts.kitchensink.serviceImpl;

/**
 * Created by Filip on 21.12.2016.
 */

import org.jboss.as.quickstarts.kitchensink.dao.LibraryDAO;
import org.jboss.as.quickstarts.kitchensink.model.Library;
import org.jboss.as.quickstarts.kitchensink.service.LibraryRegistration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class LibraryRegistrationImpl implements LibraryRegistration {

    @Inject
    private LibraryDAO libraryDAO;

    @Inject
    private Logger logger;

    @Override
    public Response register(Library library) {
        String s;
        try {
            libraryDAO.save(library);
            s = "Vytvoření knihovny: " + library.getName() + " " + library.getAddress();
            logger.info(s);
            return Response.ok().build();
        } catch (Exception e) {
            s = "Error s ukladaním: " + library.getName();
            logger.info(s);
            return Response.status(512).build();
        }
    }

    @Override
    public List<Library> getLibraries() {
            return libraryDAO.list();
    }

    @Override
    public Library findLibrary(String id) {
        return libraryDAO.find(Long.parseLong(id));
    }
}
