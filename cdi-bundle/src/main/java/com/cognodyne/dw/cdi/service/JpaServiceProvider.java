package com.cognodyne.dw.cdi.service;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.annotation.Configured;
import com.cognodyne.dw.cdi.config.CdiConfigurable;
import com.cognodyne.dw.cdi.config.JpaConfiguration;
import com.cognodyne.dw.cdi.exception.InvalidConfigurationException;
import com.google.common.collect.Maps;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class JpaServiceProvider implements JpaInjectionServices {
    private static final Logger                     logger  = LoggerFactory.getLogger(JpaServiceProvider.class);
    private ReentrantLock                           lock    = new ReentrantLock();
    private Map<String, EntityManagerFactory>       emfs    = Maps.newHashMap();
    private ThreadLocal<Map<String, EntityManager>> emCache = new ThreadLocal<Map<String, EntityManager>>() {
                                                                @Override
                                                                protected Map<String, EntityManager> initialValue() {
                                                                    return Maps.newHashMap();
                                                                }
                                                            };

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
                        logger.debug("registerPersistenceContextInjectionPoint#release called");
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
                        logger.debug("registerPersistenceUnitInjectionPoint#release called");
                    }
                };
            }
        };
    }

    @Override
    public EntityManager resolvePersistenceContext(InjectionPoint ip) {
        String name = ip.getAnnotated().getAnnotation(PersistenceContext.class).unitName();
        EntityManager em = this.emCache.get().get(name);
        if (em == null) {
            EntityManagerFactory factory = this.getEmf(name);
            if (factory != null) {
                em = factory.createEntityManager(SynchronizationType.SYNCHRONIZED);
                this.emCache.get().put(name, em);
            }
        }
        return em;
    }

    @Override
    public EntityManagerFactory resolvePersistenceUnit(InjectionPoint ip) {
        return this.getEmf(ip.getAnnotated().getAnnotation(PersistenceUnit.class).unitName());
    }

    private EntityManagerFactory getEmf(String name) {
        lock.lock();
        try {
            EntityManagerFactory emf = emfs.get(name);
            if (emf == null) {
                Optional<JpaConfiguration> config = this.getConfiguration(name);
                Optional<Environment> env = this.getEnvironment();
                if (config.isPresent() && env.isPresent()) {
                    emf = new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(config.get().toPersistenceUnitInfo(env.get())), Collections.emptyMap()).build();
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

    private Optional<JpaConfiguration> getConfiguration(String name) {
        Optional<CdiConfigurable> config = this.getConfiguration();
        if (config.isPresent() && config.get().getCdiConfiguration().isPresent()) {
            return config.get().getCdiConfiguration().get().getJpaConfigurations().stream()//
                    .filter(c -> c.getName().equals(name))//
                    .findFirst();
        }
        return Optional.empty();
    }

    @SuppressWarnings("serial")
    private Optional<CdiConfigurable> getConfiguration() {
        return Optional.ofNullable((CdiConfigurable) CDI.current().select(Configuration.class, new AnnotationLiteral<Configured>() {
        }).get());
    }

    @SuppressWarnings("serial")
    private Optional<Environment> getEnvironment() {
        return Optional.ofNullable(CDI.current().select(Environment.class, new AnnotationLiteral<Configured>() {
        }).get());
    }
}
