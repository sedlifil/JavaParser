package org.jboss.as.quickstarts.kitchensink.dao;

/**
 * Created by Filip on 09.11.2016.
 */
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

abstract class GenericDAO<T> implements DAO<T> {

    @Inject
    protected EntityManager em;

    private Class<T> type;

    GenericDAO() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class) pt.getActualTypeArguments()[0];
    }

    @Override
    public List<T> list() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> rootEntry = cq.from(type);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    @Override
    public T save(final T entity) {
        this.em.persist(entity);
        this.em.flush();
        return entity;
    }

    @Override
    public void delete(final Object id) {
        this.em.remove(this.em.getReference(type, id));
    }

    @Override
    public T find(final Object id) {
        return em.find(type, id);
    }

    @Override
    public T update(final T entity) {
        this.em.merge(entity);
        this.em.flush();
        return entity;
    }

}
