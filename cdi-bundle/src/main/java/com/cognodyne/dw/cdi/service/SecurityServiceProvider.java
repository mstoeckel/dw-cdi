package com.cognodyne.dw.cdi.service;

import java.security.Principal;

import org.jboss.weld.security.spi.SecurityServices;

public class SecurityServiceProvider implements SecurityServices {
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub
    }

    @Override
    public Principal getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }
}
