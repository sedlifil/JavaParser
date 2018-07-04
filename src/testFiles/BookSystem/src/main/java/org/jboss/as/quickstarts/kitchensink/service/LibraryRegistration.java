package org.jboss.as.quickstarts.kitchensink.service;


import org.jboss.as.quickstarts.kitchensink.model.Library;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Filip on 21.12.2016.
 */
public interface LibraryRegistration {
    Response register(Library library);

    List<Library> getLibraries();

    Library findLibrary(String id);
}
