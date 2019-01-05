package com.cognodyne.dw.example.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "PASSWORD_HISTORY")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = PasswordHistory.class)
public class PasswordHistory extends Persistent {
    private static final long serialVersionUID = -2718647897034388273L;
    private String            password;
    private User              user;

    public PasswordHistory() {
        super();
    }

    public PasswordHistory(String id) {
        super(id);
    }

    @NotNull
    @Column(name = "password", length = 64)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull
    @ManyToOne
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
