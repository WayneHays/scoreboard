package com.scoreboard.service;

import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Supplier;

public abstract class BaseTransactionalService {
    protected <T> T executeInTransaction(Supplier<T> operation, String errorMessage) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            T result = operation.get();
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(errorMessage, e);
        }
    }

    protected void executeInTransaction(Runnable operation, String errorMessage) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            operation.run();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(errorMessage, e);
        }
    }
}

