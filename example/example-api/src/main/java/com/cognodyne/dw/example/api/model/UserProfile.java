package com.cognodyne.dw.example.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

@Entity
@Table(name = "USER_PROFILE", indexes = { @Index(columnList = "first_name"), @Index(columnList = "middle_name"), @Index(columnList = "last_name"), @Index(columnList = "email"), @Index(columnList = "phone"), @Index(columnList = "organization") }, uniqueConstraints = {
        @UniqueConstraint(columnNames = { "email" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = UserProfile.class)
public class UserProfile extends Persistent {
    private static final long serialVersionUID = 2131549400651335752L;
    private String            firstName;
    private String            middleName;
    private String            lastName;
    private String            email;
    private String            phone;
    private String            organization;

    public UserProfile() {
        super();
    }

    public UserProfile(String id) {
        super(id);
    }

    @NotNull
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z'\\. _-]{0,63}$", message = "First name must be consisted of alpha character followed by 0 to 63 characters of alpha, apostrophe, period or blank space")
    @Column(name = "first_name", length = 64)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "middle_name", length = 64, nullable = true)
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @NotNull
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z'\\. _-]{0,63}$", message = "Last name must be consisted of alpha character followed by 0 to 63 characters of alpha, apostrophe, period or blank space")
    @Column(name = "last_name", length = 64)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull
    @Email
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(max = 80)
    @Column(name = "phone", nullable = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Size(max = 255)
    @Column(name = "organization", nullable = true)
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Transient
    public String getFullName() {
        return Joiner.on(" ").skipNulls().join(this.firstName, this.middleName, this.lastName);
    }

    @PrePersist
    @PreUpdate
    private void ensureEmailCase() {
        if (!Strings.isNullOrEmpty(this.email)) {
            this.email = this.email.toLowerCase();
        }
    }
}
