package com.cognodyne.dw.cdi.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.exception.InvalidConfigurationException;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Environment;

public class JpaConfiguration {
    private static final Logger            logger                  = LoggerFactory.getLogger(JpaConfiguration.class);
    @Valid
    @NotNull
    @JsonProperty("persistenceUnitName")
    private String                         persistenceUnitName;
    @Valid
    @NotNull
    @JsonProperty("transactionType")
    private PersistenceUnitTransactionType transactionType;
    @Valid
    @NotNull
    @JsonProperty("jtaDataSource")
    private Optional<DataSourceFactory>    jtaDataSourceFactory    = Optional.empty();
    @Valid
    @NotNull
    @JsonProperty("nonJtaDataSource")
    private Optional<DataSourceFactory>    nonJtaDataSourceFactory = Optional.empty();
    @Valid
    @NotNull
    @JsonProperty("mappingFileNames")
    private List<String>                   mappingFileNames        = Collections.emptyList();
    @Valid
    @NotNull
    @JsonProperty("jarFilePaths")
    private List<String>                   jarFilePaths            = Collections.emptyList();
    @Valid
    @NotNull
    @JsonProperty("persistenceUnitRootPath")
    private Optional<String>               persistenceUnitRootPath = Optional.empty();
    @Valid
    @NotNull
    @JsonProperty("managedClassNames")
    private List<String>                   managedClassNames       = Collections.emptyList();
    @Valid
    @NotNull
    @JsonProperty("excludeUnlistedClasses")
    private Boolean                        excludeUnlistedClasses  = false;
    @Valid
    @NotNull
    @JsonProperty("sharedCacheMode")
    private SharedCacheMode                sharedCacheMode         = SharedCacheMode.UNSPECIFIED;
    @Valid
    @NotNull
    @JsonProperty("validationMode")
    private ValidationMode                 validationMode          = ValidationMode.AUTO;
    @Valid
    @NotNull
    @JsonProperty("properties")
    private Properties                     properties              = new Properties();

    public JpaConfiguration() {
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

    public Optional<DataSourceFactory> getJtaDataSourceFactory() {
        return jtaDataSourceFactory;
    }

    public Optional<DataSourceFactory> getNonJtaDataSourceFactory() {
        return nonJtaDataSourceFactory;
    }

    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    public List<String> getJarFilePaths() {
        return jarFilePaths;
    }

    public Optional<String> getPersistenceUnitRootPath() {
        return persistenceUnitRootPath;
    }

    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    public Boolean getExcludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    public SharedCacheMode getSharedCacheMode() {
        return sharedCacheMode;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public Properties getProperties() {
        return properties;
    }

    public PersistenceUnitInfo toPersistenceUnitInfo(Environment environment) throws InvalidConfigurationException {
        if (!this.jtaDataSourceFactory.isPresent() && !this.nonJtaDataSourceFactory.isPresent()) {
            throw new InvalidConfigurationException("One of jtaDataSourceFactory or nonJtaDataSourceFactory is required");
        }
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return JpaConfiguration.this.getPersistenceUnitName();
            }

            @Override
            public String getPersistenceProviderClassName() {
                return HibernatePersistenceProvider.class.getName();
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return JpaConfiguration.this.getTransactionType();
            }

            @Override
            public DataSource getJtaDataSource() {
                return JpaConfiguration.this.getJtaDataSourceFactory().isPresent() ? JpaConfiguration.this.getJtaDataSourceFactory().get().build(environment.metrics(), "JTA Datasource for " + JpaConfiguration.this.getPersistenceUnitName()) : null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return JpaConfiguration.this.getNonJtaDataSourceFactory().isPresent() ? JpaConfiguration.this.getNonJtaDataSourceFactory().get().build(environment.metrics(), "Non-JTA Datasource for " + JpaConfiguration.this.getPersistenceUnitName()) : null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return JpaConfiguration.this.getMappingFileNames();
            }

            @Override
            public List<URL> getJarFileUrls() {
                return JpaConfiguration.this.getJarFilePaths().stream()//
                        .map(path -> {
                            try {
                                return new File(path).toURI().toURL();
                            } catch (MalformedURLException e) {
                                logger.error("Unable to convert {} to URL", path, e);
                                return null;
                            }
                        })//
                        .filter(url -> url != null)//
                        .collect(Collectors.toList());
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                try {
                    return JpaConfiguration.this.getPersistenceUnitRootPath().isPresent() ? new File(JpaConfiguration.this.getPersistenceUnitRootPath().get()).toURI().toURL() : null;
                } catch (MalformedURLException e) {
                    logger.error("Unable to convert {} to URL", JpaConfiguration.this.getPersistenceUnitRootPath().get(), e);
                    return null;
                }
            }

            @Override
            public List<String> getManagedClassNames() {
                return JpaConfiguration.this.getManagedClassNames();
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return JpaConfiguration.this.excludeUnlistedClasses;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return JpaConfiguration.this.getSharedCacheMode();
            }

            @Override
            public ValidationMode getValidationMode() {
                return JpaConfiguration.this.getValidationMode();
            }

            @Override
            public Properties getProperties() {
                return JpaConfiguration.this.getProperties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return "2.1";
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {
                // no op
            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }
}
