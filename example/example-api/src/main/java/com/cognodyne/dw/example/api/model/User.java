package com.cognodyne.dw.example.api.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.MoreObjects;

@Entity
@Table(name = "USERS", indexes = { @Index(columnList = "account_status"), @Index(columnList = "user_profile_id") }, uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
public class User extends Persistent {
    private static final long serialVersionUID = 387329933549619471L;

    public enum AccountStatus {
        Active, Locked, Deleted
    }

    private String                username;
    private String                salt;
    private String                password;
    private AccountStatus         accountStatus;
    private DateTime              passwordExpiration;
    private Integer               retries;
    private UserProfile           profile;
    private List<PasswordHistory> passwordHistory;
    private List<Group>           groups;

    public User() {
        super();
    }

    public User(String id) {
        super(id);
    }

    @NotNull
    //@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{2,63}$", message = "Username must be consisted of alpha character followed by 2 to 63 characters of alpha-numeric")
    @Column(name = "username", length = 64)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    @NotNull
    @Column(name = "salt", length = 64)
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 16)
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "password_exp_ts")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getPasswordExpiration() {
        return passwordExpiration;
    }

    public void setPasswordExpiration(DateTime passwordExpiration) {
        this.passwordExpiration = passwordExpiration;
    }

    @NotNull
    @Column(name = "retries")
    public Integer getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    @NotNull
    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id")
    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    @JsonIgnore
    @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "user")
    public List<PasswordHistory> getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(List<PasswordHistory> passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    @ManyToMany
    @JoinTable(name = "USER_GROUP_XREF", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"), uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "group_id" }) })
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())//
                .add("id", this.id)//
                .add("username", this.username)//
                .add("salt", this.salt)//
                .add("password", this.password)//
                .add("accountStatus", this.accountStatus)//
                .add("passwordExpiration", this.passwordExpiration)//
                .add("retries", this.retries)//
                .add("profile", this.profile)//
                .add("passwordHistory", this.passwordHistory)//
                .add("groups", this.groups == null ? "[]" : this.groups.stream().map(g -> g.getName()).collect(Collectors.toList()))//
                .toString();
    }
}
