package com.cognodyne.dw.cdi.example.service;

import java.util.Optional;

import javax.transaction.Transactional;

import com.cognodyne.dw.example.api.model.UserProfile;

public class UserProfileService extends PersistentService<UserProfile> {
    @Transactional
    public Optional<UserProfile> find(String id) {
        return Optional.ofNullable(this.em.find(UserProfile.class, id));
    }
}
