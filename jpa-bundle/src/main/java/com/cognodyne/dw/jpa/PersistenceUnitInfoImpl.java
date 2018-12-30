package com.cognodyne.dw.jpa;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.dropwizard.db.DataSourceFactory;

public class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
    @Valid
    @JsonProperty("excludeUnlistedClasses")
    private boolean                excludeUnlistedClasses = false;
    @Valid
    @JsonProperty("jarFileUrls")
    private List<URL>              jarFileUrls            = Lists.newArrayList();
    @Valid
    @NotNull
    @JsonProperty("jtaDataSource")
    private DataSourceFactory      jtaDataSource;
    //
    private List<ClassTransformer> classTransformers      = Lists.newArrayList();

    @Override
    public void addTransformer(ClassTransformer transformer) {
        this.classTransformers.add(transformer);
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return this.excludeUnlistedClasses;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public List<URL> getJarFileUrls() {
        return ImmutableList.copyOf(this.jarFileUrls);
    }

    @Override
    public DataSource getJtaDataSource() {
        //return this.jtaDataSource.
        return null; //TODO
    }

    @Override
    public List<String> getManagedClassNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getMappingFileNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPersistenceProviderClassName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPersistenceUnitName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValidationMode getValidationMode() {
        // TODO Auto-generated method stub
        return null;
    }
}
