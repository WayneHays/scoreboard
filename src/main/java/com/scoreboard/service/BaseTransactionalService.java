package com.scoreboard.service;

import com.scoreboard.exception.NotFoundException;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.config.hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Supplier;

public abstract class BaseTransactionalService {

    protected <T> T executeInTransaction(Supplier<T> operation, String errorMessage) {
        return executeCommon(operation, errorMessage);
    }

    protected void executeInTransaction(Runnable operation, String errorMessage) {
        executeCommon(() -> {
            operation.run();
            return null;
        }, errorMessage);
    }

    private <T> T executeCommon(Supplier<T> operation, String errorMessage) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            T result = operation.get();
            transaction.commit();
            return result;

        } catch (NotFoundException | ValidationException e) {
            transaction.rollback();
            throw e;

        } catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(errorMessage, e);
        }
    }
}

