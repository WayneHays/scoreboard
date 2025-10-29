package com.scoreboard.dao;

import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public abstract class BaseDao<T> {

    public void save(T entity) {
        getCurrentSession().persist(entity);
    }

    protected Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    protected <E> Query<E> createPaginatedQuery(String hql, Class<E> entityClass,
                                                int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be positive");
        }

        return getCurrentSession()
                .createQuery(hql, entityClass)
                .setMaxResults(pageSize)
                .setFirstResult((pageNumber - 1) * pageSize);
    }
}
