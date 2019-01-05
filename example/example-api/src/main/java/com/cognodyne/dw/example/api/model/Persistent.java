package com.cognodyne.dw.example.api.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class Persistent implements Serializable {
    private static final long serialVersionUID = -5505262903585548886L;
    protected String          id;
    protected User            createdBy;
    protected DateTime        createdAt;
    protected User            lastModifiedBy;
    protected DateTime        lastModifiedAt;

    protected Persistent() {
    }

    protected Persistent(String id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "CREATED_AT", nullable = false)
    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "LAST_MODIFIED_BY", nullable = false)
    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "LAST_MODIFIED_AT", nullable = false)
    public DateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(DateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Persistent that = (Persistent) obj;
        return Objects.equals(this.id, that.id);
    }
}
