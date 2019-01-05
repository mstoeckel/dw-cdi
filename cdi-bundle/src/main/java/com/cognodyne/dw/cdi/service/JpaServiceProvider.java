package com.cognodyne.dw.cdi.service;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.CdiConfigurable;
import com.cognodyne.dw.cdi.InvalidConfigurationException;
import com.cognodyne.dw.cdi.annotation.Configured;
import com.google.common.collect.Maps;

import io.dropwizard.setup.Environment;

public class JpaServiceProvider implements JpaInjectionServices {
    private static final Logger               logger = LoggerFactory.getLogger(JpaServiceProvider.class);
    private ReentrantLock                     lock   = new ReentrantLock();
    private Map<String, EntityManagerFactory> emfs   = Maps.newHashMap();

    public void cleanup() {
        logger.debug("cleanup called");
        for (Entry<String, EntityManagerFactory> entry : emfs.entrySet()) {
            entry.getValue().close();
        }
    }

    @Override
    public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint(InjectionPoint ip) {
        return new ResourceReferenceFactory<EntityManager>() {
            @Override
            public ResourceReference<EntityManager> createResource() {
                return new ResourceReference<EntityManager>() {
                    @Override
                    public EntityManager getInstance() {
                        return resolvePersistenceContext(ip);
                    }

                    @Override
                    public void release() {
                        //noop
                    }
                };
            }
        };
    }

    @Override
    public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint(InjectionPoint ip) {
        return new ResourceReferenceFactory<EntityManagerFactory>() {
            @Override
            public ResourceReference<EntityManagerFactory> createResource() {
                return new ResourceReference<EntityManagerFactory>() {
                    @Override
                    public EntityManagerFactory getInstance() {
                        return resolvePersistenceUnit(ip);
                    }

                    @Override
                    public void release() {
                        //noop
                    }
                };
            }
        };
    }

    @Override
    public EntityManager resolvePersistenceContext(InjectionPoint ip) {
        EntityManagerFactory factory = this.resolvePersistenceUnit(ip);
        if (factory != null) {
            return factory.createEntityManager();
        }
        return null;
    }

    @SuppressWarnings("serial")
    @Override
    public EntityManagerFactory resolvePersistenceUnit(InjectionPoint ip) {
        String name = ip.getAnnotated().getAnnotation(PersistenceContext.class).unitName();
        logger.debug("resolving persistence unit for {}...", name);
        lock.lock();
        try {
            EntityManagerFactory emf = emfs.get(name);
            if (emf == null) {
                CdiConfigurable config = CDI.current().select(CdiConfigurable.class, new AnnotationLiteral<Configured>() {
                }).get();
                Environment env = CDI.current().select(Environment.class, new AnnotationLiteral<Configured>() {
                }).get();
                if (config.getCdiConfiguration().isPresent() && config.getCdiConfiguration().get().getJpaConfiguration().isPresent()) {
                    //emf = Persistence.createEntityManagerFactory(name);
                    emf = new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(config.getCdiConfiguration().get().getJpaConfiguration().get().toPersistenceUnitInfo(env)), Collections.emptyMap()).build();
                    emfs.put(name, emf);
                } else {
                    logger.error("jpa is not configured");
                }
            }
            return emf;
        } catch (InvalidConfigurationException e) {
            logger.error("error while creating emf", e);
            return null;
        } finally {
            lock.unlock();
        }
    }
}
