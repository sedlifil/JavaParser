package org.jboss.as.quickstarts.kitchensink.dao;

/**
 * Created by Filip on 09.11.2016.
 */
import java.util.List;

interface DAO<T> {

    List<T> list();

    T save(T entity);

    void delete(Object id);

    T find(Object id);

    T update(T entity);
}
