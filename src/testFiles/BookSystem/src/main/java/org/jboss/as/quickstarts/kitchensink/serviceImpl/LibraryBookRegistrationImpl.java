package org.jboss.as.quickstarts.kitchensink.serviceImpl;



import org.jboss.as.quickstarts.kitchensink.dao.LibraryDAO;
import org.jboss.as.quickstarts.kitchensink.model.Book;
import org.jboss.as.quickstarts.kitchensink.model.Library;
import org.jboss.as.quickstarts.kitchensink.service.LibraryBookRegistration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Filip on 03.12.2016.
 */
@Stateless
public class LibraryBookRegistrationImpl implements LibraryBookRegistration {
    private static final int MAX_SAME_BOOK = 5;
    @Inject
    private LibraryDAO libraryDAO;

    @Inject
    private Logger logger;


    public boolean mayAddSameBook(Library library, Book book) {
        int count = 0;
        // count how many same books with book are in library
        for(Book iter: library.getBooks()){
            if(iter.getIsbn().equals(book.getIsbn())){
                count++;
            }
        }
        if(count < MAX_SAME_BOOK){
            return true;
        }
        return false;
    }

    @Override
    public Response registerBook(Library library, Book book) {
        String s;
            if(mayAddSameBook(library, book)){
                Set<Book> tmpList = library.getBooks();
                tmpList.add(book);
                library.setBooks(tmpList);
                tmpList.forEach(book1 -> System.out.println(book1.getName()));
                try {
                    libraryDAO.update(library);
                    s = "Přidání knihy " + book.getName() +" do " + library.getName();
                    logger.info(s);
                    return Response.ok().build();
                }catch (Exception e){
                    s = "Nelze přidat knihu " + book.getName() +" do  " + library.getName();
                    logger.info(s);
                    return Response.status(511).build();
                }
            }else{
                s = "Kniha " + book.getName() +" už je 5x v " + library.getName() + "." +
                         "Nelze přidat do knihovny!";
                logger.info(s);
                return Response.status(512).build();
            }
    }

}
