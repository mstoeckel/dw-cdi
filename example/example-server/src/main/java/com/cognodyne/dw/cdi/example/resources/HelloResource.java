package com.cognodyne.dw.cdi.example.resources;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.cognodyne.dw.cdi.example.service.UserProfileService;
import com.cognodyne.dw.cdi.example.service.UserService;
import com.cognodyne.dw.example.api.model.User;
import com.cognodyne.dw.example.api.model.User.AccountStatus;
import com.cognodyne.dw.example.api.model.UserProfile;
import com.cognodyne.dw.example.api.service.HelloService;

@Singleton
public class HelloResource extends BaseResource implements HelloService {
    @Inject
    private Logger             logger;
    @Inject
    private UserService        userService;
    @Inject
    private UserProfileService userProfileService;

    @Override
    @Transactional
    public String sayHello() {
        logger.debug("config:{}", this.getDwCdiExampleConfiguration());
        UserProfile profile = new UserProfile();
        profile.setFirstName("Michael");
        profile.setMiddleName("M");
        profile.setLastName("Stoeckel");
        profile.setEmail("mstoeckel@cognodyne.com");
        profile = this.userProfileService.create(profile);
        User user = new User();
        user.setUsername("mstoeckel");
        user.setAccountStatus(AccountStatus.Active);
        user.setSalt("fasfdasdf");
        user.setPassword("foo00000oooooo");
        user.setPasswordExpiration(DateTime.now().plusDays(365));
        user.setRetries(0);
        user.setProfile(profile);
        userService.create(user);
        List<User> users = this.userService.findAll();
        return "Hello! Found " + users.size() + " users";
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
