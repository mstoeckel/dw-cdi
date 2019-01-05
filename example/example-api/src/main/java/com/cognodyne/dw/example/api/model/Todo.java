package com.cognodyne.dw.example.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "TODO", indexes = { @Index(columnList = "description"), @Index(columnList = "done"), @Index(columnList = "due") }, uniqueConstraints = { @UniqueConstraint(columnNames = { "description", "done", "due" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
public class Todo extends Persistent {
    private static final long serialVersionUID = 2316246728633167594L;
    private String            description;
    private boolean           done;
    private DateTime          due;

    public Todo() {
    }

    public Todo(String id) {
        super(id);
    }

    @Column(name = "description", length = 256, nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "done", nullable = false)
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "due", nullable = false)
    public DateTime getDue() {
        return due;
    }

    public void setDue(DateTime due) {
        this.due = due;
    }
}
