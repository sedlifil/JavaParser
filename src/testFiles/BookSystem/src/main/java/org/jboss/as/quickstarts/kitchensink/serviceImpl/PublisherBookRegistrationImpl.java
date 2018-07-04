package org.jboss.as.quickstarts.kitchensink.serviceImpl;



import org.jboss.as.quickstarts.kitchensink.dao.BookDAO;
import org.jboss.as.quickstarts.kitchensink.dao.PublisherDAO;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.model.Book;
import org.jboss.as.quickstarts.kitchensink.model.Publisher;
import org.jboss.as.quickstarts.kitchensink.service.PublisherBookRegistration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by Filip on 03.12.2016.
 */
@Stateless
public class PublisherBookRegistrationImpl implements PublisherBookRegistration {


    @Inject
    private PublisherDAO publisherDAO;

    @Inject
    private BookDAO bookDAO;

    @Inject
    private Logger logger;

    public PublisherBookRegistrationImpl() {
    }

    @Override
    public Response registerBook(Publisher publisher, Book book) {
        String info;
        if(checkAuthors(publisher, book)){
            book.setPublisher(publisher);
            bookDAO.update(book);
            info = "Přidání knihy " + book.getName() +" do " + publisher.getName();
            logger.info(info);
            return Response.ok().build();
        }else{
            info = "Nelze přidat knihu " + book.getName() +". Všichni autoři nemají smlouvu s "
                    + publisher.getName();
            logger.info(info);
            return Response.status(512).build();
        }
    }

    private boolean checkAuthors(Publisher publisher, Book book) {
            boolean flagFindAuthor = false;

            for (Author itAuthorPb : publisher.getAuthors()) {
                if (book.getAuthor().getId() == itAuthorPb.getId()) {
                    flagFindAuthor = true;
                    break;
                }
            }
            return flagFindAuthor;
        }


    @Override
    public Publisher findPublisher(String id) {
        return publisherDAO.find(Long.parseLong(id));
    }

    @Override
    public Book findBook(String id) {
        return bookDAO.find(Long.parseLong(id));
    }
}