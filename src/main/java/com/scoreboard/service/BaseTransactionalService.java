package com.scoreboard.service;

import com.scoreboard.exception.DaoException;
import com.scoreboard.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Supplier;

@Slf4j
public class BaseTransactionalService {

    public <T> T executeInTransaction(Supplier<T> operation) {
        return executeCommon(operation);
    }

    public void executeInTransaction(Runnable operation) {
        executeCommon(() -> {
            operation.run();
            return null;
        });
    }

    private <T> T executeCommon(Supplier<T> operation) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            T result = operation.get();
            transaction.commit();
            return result;
        } catch (Exception e) {
            safeRollback(transaction, e);
            throw new DaoException("Failed to execute DB request",e);
        }
    }

    private void safeRollback(Transaction transaction, Exception originalException) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}

