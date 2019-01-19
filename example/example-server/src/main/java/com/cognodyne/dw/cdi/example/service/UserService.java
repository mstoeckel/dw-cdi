package com.cognodyne.dw.cdi.example.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;

import com.cognodyne.dw.example.api.model.User;

public class UserService extends PersistentService<User> {
    @Transactional
    public Optional<User> find(String id) {
        return this.find(User.class, id);
    }

    @Transactional
    public List<User> findAll() {
        return this.em.createQuery("select u from User u", User.class).getResultList();
    }

    @Transactional
    public Optional<User> getAdminUser() {
        try {
            return Optional.ofNullable(this.em.createQuery("select u from User u where u.username=:username", User.class).setParameter("username", "admin").getSingleResult());
        } catch (NoResultException | NonUniqueResultException | IllegalStateException e) {
            return Optional.empty();
        }
    }

    public Optional<User> getCurrentUser() {
        return Optional.empty();
    }
}
