package com.cognodyne.dw.cdi.example.resources;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.cognodyne.dw.example.api.HelloService;

public class HelloResource implements HelloService {
    @Inject
    private BeanManager manager;

    @Override
    public String sayHello() {
        return "Hello!" + this.manager;
    }
}
