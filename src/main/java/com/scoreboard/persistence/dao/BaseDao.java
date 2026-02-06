package com.scoreboard.persistence.dao;

import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

public abstract class BaseDao {
    protected Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
}
