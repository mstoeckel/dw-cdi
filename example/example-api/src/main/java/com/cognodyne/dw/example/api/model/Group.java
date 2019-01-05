package com.cognodyne.dw.example.api.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.MoreObjects;

@Entity
@Table(name = "GROUPS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Group.class)
public class Group extends Persistent {
    private static final long serialVersionUID = 7566801867097172025L;
    private String     name;
    private String     description;
    private List<User> users;

    public Group() {
        super();
    }

    public Group(String id) {
        super(id);
    }

    @NotNull
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{0,63}$", message = "Group name must be consisted of alpha character followed by 0 to 63 characters of alpha-numeric")
    @Column(name = "name", length = 64)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", length = 256)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany
    @JoinTable(name = "USER_GROUP_XREF", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "user_id"), uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "group_id" }) })
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())//
                .add("id", this.id)//
                .add("name", this.name)//
                .add("description", this.description)//
                .add("users", this.users == null ? "[]" : this.users.stream().map(u -> u.getUsername()).collect(Collectors.toList()))//
                .toString();
    }
}
