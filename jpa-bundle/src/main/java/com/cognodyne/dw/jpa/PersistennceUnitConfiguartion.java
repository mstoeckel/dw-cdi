package com.cognodyne.dw.jpa;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.dropwizard.db.DataSourceFactory;

public class PersistennceUnitConfiguartion {
    @Valid
    @JsonProperty("excludeUnlistedClasses")
    private boolean             excludeUnlistedClasses       = false;
    @Valid
    @JsonProperty("jarFileUrls")
    private List<URL>           jarFileUrls                  = Lists.newArrayList();
    @Valid
    @JsonProperty("jtaDataSource")
    private DataSourceFactory   jtaDataSource;
    @Valid
    @JsonProperty("nonJtaDataSource")
    private DataSourceFactory   nonJtaDataSource;
    @Valid
    @JsonProperty("managedClassNames")
    private List<String>        managedClassNames            = Lists.newArrayList();
    @Valid
    @JsonProperty("MappingFileNames")
    private List<String>        mappingFileNames             = Lists.newArrayList();
    @Valid
    @NotNull
    @JsonProperty("persistenceProviderClassName")
    private String              persistenceProviderClassName = "org.hibernate.jpa.HibernatePersistenceProvider";
    @Valid
    @NotNull
    @JsonProperty("persistenceUnitName")
    private String              persistenceUnitName;
    @Valid
    @JsonProperty("persistenceUnitRootUrl")
    private URL                 persistenceUnitRootUrl;
    @Valid
    @NotNull
    @JsonProperty("properties")
    private Map<String, String> properties                   = Maps.newHashMap();
    @Valid
    @NotNull
    @JsonProperty("sharedCacheMode")
    private String              sharedCacheMode              = "NONE";
    @Valid
    @NotNull
    @JsonProperty("transactionType")
    private String              transactionType              = "RESOURCE_LOCAL";
    @Valid
    @NotNull
    @JsonProperty("validationMode")
    private String              validationMode               = "AUTO";

    public boolean excludeUnlistedClasses() {
        return this.excludeUnlistedClasses;
    }

    public List<URL> getJarFileUrls() {
        return ImmutableList.copyOf(this.jarFileUrls);
    }

    public DataSourceFactory getJtaDataSource() {
        return this.jtaDataSource;
    }

    public List<String> getManagedClassNames() {
        return ImmutableList.copyOf(this.managedClassNames);
    }

    public List<String> getMappingFileNames() {
        return ImmutableList.copyOf(this.mappingFileNames);
    }

    public DataSourceFactory getNonJtaDataSource() {
        return this.nonJtaDataSource;
    }

    public String getPersistenceProviderClassName() {
        return this.persistenceProviderClassName;
    }

    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    public URL getPersistenceUnitRootUrl() {
        return this.persistenceUnitRootUrl;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.valueOf(this.sharedCacheMode);
    }

    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.valueOf(this.transactionType);
    }

    public ValidationMode getValidationMode() {
        return ValidationMode.valueOf(this.validationMode);
    }
}
