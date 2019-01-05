package com.cognodyne.dw.cdi;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.cognodyne.dw.cdi.jpa.JpaConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableSet;

@JsonDeserialize(builder = CdiConfiguration.Builder.class)
public class CdiConfiguration {
    private final Set<PathMatcher>           includes;
    private final Set<PathMatcher>           excludes;
    private final Optional<JpaConfiguration> jpaConfiguartion;

    private CdiConfiguration(Set<PathMatcher> includes, Set<PathMatcher> excludes, Optional<JpaConfiguration> jpaConfiguartion) {
        this.includes = includes;
        this.excludes = excludes;
        this.jpaConfiguartion = jpaConfiguartion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<PathMatcher> getIncludes() {
        return includes;
    }

    public Set<PathMatcher> getExcludes() {
        return excludes;
    }

    public Optional<JpaConfiguration> getJpaConfiguration() {
        return this.jpaConfiguartion;
    }

    boolean include(Class<?> cls) {
        Path path = FileSystems.getDefault().getPath(cls.getName().replaceAll("\\.", "/"));
        if (this.excludes.stream().anyMatch(p -> p.matches(path))) {
            return this.includes.stream().anyMatch(p -> p.matches(path));
        }
        return true;
    }

    public static final class Builder {
        @JsonProperty
        private List<String>               includes = Collections.emptyList();
        @JsonProperty
        private List<String>               excludes = Collections.emptyList();
        @JsonProperty
        @Valid
        private Optional<JpaConfiguration> jpa      = Optional.empty();

        private Builder() {
        }

        public CdiConfiguration build() {
            ImmutableSet.Builder<PathMatcher> includesBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<PathMatcher> excludesBuilder = ImmutableSet.builder();
            this.includes.stream().forEach(str -> includesBuilder.add(FileSystems.getDefault().getPathMatcher("glob:" + str)));
            this.excludes.stream().forEach(str -> excludesBuilder.add(FileSystems.getDefault().getPathMatcher("glob:" + str)));
            return new CdiConfiguration(includesBuilder.build(), excludesBuilder.build(), this.jpa);
        }

        public Builder includes(List<String> includes) {
            if (includes != null) {
                this.includes = includes;
            }
            return this;
        }

        public Builder excludes(List<String> excludes) {
            if (excludes != null) {
                this.excludes = excludes;
            }
            return this;
        }

        public Builder jpa(JpaConfiguration jpa) {
            this.jpa = Optional.ofNullable(jpa);
            return this;
        }
    }
}
