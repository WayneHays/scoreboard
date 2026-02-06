package com.scoreboard.persistence.database;

import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

public abstract class BaseDao {

    protected Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
}
