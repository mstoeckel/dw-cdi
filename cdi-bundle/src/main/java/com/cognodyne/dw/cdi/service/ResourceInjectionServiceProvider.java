package com.cognodyne.dw.cdi.service;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.injection.spi.helpers.AbstractResourceServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceInjectionServiceProvider extends AbstractResourceServices {
    private static final Logger logger = LoggerFactory.getLogger(ResourceInjectionServiceProvider.class);
    private Context             ctx;

    public ResourceInjectionServiceProvider() {
        try {
            ctx = new InitialContext();
            logger.debug("ctx created");
        } catch (NamingException e) {
            logger.error("Unable to create initial context", e);
        }
    }

    @Override
    protected Context getContext() {
        return this.ctx;
    }
}
