package com.cognodyne.dw.cdi.service;

import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.cognodyne.dw.cdi.JndiHelper;

public class JtaServiceProvider implements TransactionServices {
    private static final Logger         logger = LoggerFactory.getLogger(JtaServiceProvider.class);
    private volatile JTAEnvironmentBean jtaEnv;

    public JtaServiceProvider() {
        jtaEnv = jtaPropertyManager.getJTAEnvironmentBean();
        jtaEnv.setTransactionManagerClassName(TransactionManagerImple.class.getName());
        try {
            JndiHelper.bind(jtaEnv.getUserTransactionJNDIContext(), jtaEnv.getUserTransaction());
            JndiHelper.bind("java:comp/UserTransaction", jtaEnv.getUserTransaction());
            JndiHelper.bind(jtaEnv.getTransactionManagerJNDIContext(), jtaEnv.getTransactionManager());
            JndiHelper.bind(jtaEnv.getTransactionSynchronizationRegistryJNDIContext(), jtaEnv.getTransactionSynchronizationRegistry());
            logger.info("Successfully intialized {}", this.getClass().getName());
        } catch (NamingException ex) {
            logger.error("Unable to init", ex);
        }
    }

    public void cleanup() {
        logger.debug("cleanup called");
        // TODO Auto-generated method stub
    }

    @Override
    public void registerSynchronization(Synchronization synchronizedObserver) {
        logger.debug("registerSynchronization called");
        TransactionSynchronizationRegistry reg = (TransactionSynchronizationRegistry) JndiHelper.lookup(jtaEnv.getTransactionSynchronizationRegistryJNDIContext());
        if (reg != null) {
            reg.registerInterposedSynchronization(synchronizedObserver);
        }
    }

    @Override
    public boolean isTransactionActive() {
        try {
            return getUserTransaction().getStatus() == Status.STATUS_ACTIVE;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public UserTransaction getUserTransaction() {
        logger.debug("getUserTransaction called");
        return (UserTransaction) JndiHelper.lookup(jtaEnv.getUserTransactionJNDIContext());
    }
}
