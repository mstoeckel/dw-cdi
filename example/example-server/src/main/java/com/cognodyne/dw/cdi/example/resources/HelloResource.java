package com.cognodyne.dw.cdi.example.resources;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.annotation.Configured;
import com.cognodyne.dw.example.api.model.User;
import com.cognodyne.dw.example.api.service.HelloService;

import io.dropwizard.setup.Environment;

@Singleton
public class HelloResource extends BaseResource implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloResource.class);
    @Inject
    private BeanManager         manager;
    @PersistenceContext(unitName = "exampleUnit")
    private EntityManager       em;
    @Inject
    @Configured
    private Environment         env;

    @Override
    @Transactional
    public String sayHello() {
        logger.debug("em:{}", em);
        logger.debug("config:{}", this.getDwCdiExampleConfiguration());
        logger.debug("env:{}", env);
        List<User> users = this.em.createQuery("select u from User u", User.class).getResultList();
        return "Hello! Found " + users.size() + " users. bm:" + this.manager;
    }

    @PostConstruct
    public void onInit() {
        logger.debug("onInit...");
    }

    @PreDestroy
    public void onDestroy() {
        logger.debug("onDestroy...");
    }
}
