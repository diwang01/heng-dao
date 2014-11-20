package org.hengdao.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * 事务包装类
 * Created by wangdi on 14-11-20.
 * @author  barney.wang
 */
public class TransactionWrapper {


    private final PlatformTransactionManager transactionManager;

    private final TransactionAttribute transactionAttribute;

    private final String joinpointIdentification;

    private TransactionStatus transactionStatus;

    public TransactionWrapper(PlatformTransactionManager transactionManager,
                               TransactionAttribute transactionAttribute, String joinpointIdentification) {
        this.transactionManager = transactionManager;
        this.transactionAttribute = transactionAttribute;
        this.joinpointIdentification = joinpointIdentification;
    }

    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    public TransactionAttribute getTransactionAttribute() {
        return this.transactionAttribute;
    }

    /**
     * Return a String representation of this joinpoint (usually a Method call) for use in logging.
     */
    public String getJoinpointIdentification() {
        return this.joinpointIdentification;
    }

    public void newTransactionStatus(TransactionStatus status) {
        this.transactionStatus = status;
    }

    public TransactionStatus getTransactionStatus() {
        return this.transactionStatus;
    }

    /**
     * Return whether a transaction was created by this aspect, or whether we just have a placeholder to keep
     * ThreadLocal stack integrity.
     */
    public boolean hasTransaction() {
        return (this.transactionStatus != null);
    }

    @Override
    public String toString() {
        return this.transactionAttribute.toString();
    }

    public TransactionWrapper newCopy() {
        return new TransactionWrapper(this.transactionManager, this.transactionAttribute, this.joinpointIdentification);
    }
}
