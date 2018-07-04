package org.jboss.as.quickstarts.kitchensink.serviceImpl;



import org.jboss.as.quickstarts.kitchensink.dao.BookDAO;
import org.jboss.as.quickstarts.kitchensink.model.Author;
import org.jboss.as.quickstarts.kitchensink.model.Book;
import org.jboss.as.quickstarts.kitchensink.service.BookRegistration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Filip on 22.12.2016.
 */
@Stateless
public class BookRegistrationImpl implements BookRegistration {

    @Inject
    private BookDAO bookDAO;


    @Inject
    private Logger logger;

    @Override
    public Book findBook(String id) {
        return bookDAO.find(Long.parseLong(id));
    }

    @Override
    public Response register(Book book) {
        String s;
        if (book.getAuthor() != null && book.getPublisher() != null) {
            if (checkISBN(book)) {
                if (checkAuthors(book)) {
                    try {
                        bookDAO.update(book);
                        s = "Vytvoření knihy: " + book.getName();
                        logger.info(s);
                        return Response.ok().build();
                    } catch (Exception e) {
                        s = "Error s ukládáním knihy: " + book.getName() ;
                        logger.info(s);
                        return Response.status(511).build();
                    }

                } else {
                    s = "Všichni autoři z knihy : " + book.getName() +
                            " nemají smlouvu s nakladatelstvím " + book.getPublisher().getName();
                    logger.info(s);
                    return Response.status(512).build();
                }
            }else {
                s = "ISBN: " + book.getIsbn() + " z knihy: " + book.getName() +
                        " je už používáno!";
                logger.info(s);
                return Response.status(513).build();
            }


        } else {
            s = "Kniha: " + book.getName() +
                    " musí mít nejméně jednoho autora a jedno nakladatelství!";
            logger.info(s);
            return Response.status(514).build();
        }
    }
    private boolean checkISBN(Book book) {
        for (Book itBook : bookDAO.list()) {
            if (itBook.getIsbn().equals(book.getIsbn())) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAuthors(Book book) {
        boolean flagFindAuthor = false;

            for (Author itAuthorPb : book.getPublisher().getAuthors()) {
                if (book.getAuthor().getId() == itAuthorPb.getId()) {
                    flagFindAuthor = true;
                    break;
                }
            }
        return flagFindAuthor;
    }


    @Override
    public List<Book> getBooks() {
        return bookDAO.list();
    }
}