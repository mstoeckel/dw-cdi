package com.cognodyne.dw.cdi.example.service;

import java.util.Optional;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.cognodyne.dw.example.api.model.Persistent;
import com.cognodyne.dw.example.api.model.User;

public abstract class PersistentService<T extends Persistent> {
    @Inject
    protected Logger        logger;
    @PersistenceContext(unitName = "exampleUnit")
    protected EntityManager em;

    @Transactional
    public abstract Optional<T> find(String id);

    public <C> Optional<C> find(Class<C> cls, String id) {
        return Optional.ofNullable(this.em.find(cls, id));
    }

    @Transactional
    public T create(T entity) {
        onPrePersist(entity);
        em.persist(entity);
        return entity;
    }

    @Transactional
    public T update(T entity) {
        onPreUpdate(entity);
        em.merge(entity);
        return entity;
    }

    @Transactional
    public void remove(T entity) {
        em.remove(entity);
    }

    protected void onPrePersist(Persistent obj) {
        User user = this.getUser();
        DateTime now = DateTime.now();
        obj.setCreatedBy(user);
        obj.setCreatedAt(now);
        obj.setLastModifiedBy(user);
        obj.setLastModifiedAt(now);
    }

    protected void onPreUpdate(Persistent obj) {
        User user = this.getUser();
        obj.setLastModifiedBy(user);
        obj.setLastModifiedAt(new DateTime());
    }

    protected User getUser() {
        UserService userService = CDI.current().select(UserService.class).get();
        Optional<User> user = userService.getCurrentUser();
        if (!user.isPresent()) {
            user = userService.getAdminUser();
        }
        return user.orElse(null);
    }
}
