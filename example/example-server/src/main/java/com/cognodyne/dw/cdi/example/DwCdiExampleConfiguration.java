package com.cognodyne.dw.cdi.example;

import java.util.Optional;

import javax.validation.Valid;

import com.cognodyne.dw.cdi.CdiConfigurable;
import com.cognodyne.dw.cdi.CdiConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class DwCdiExampleConfiguration extends Configuration implements CdiConfigurable {
    @Valid
    @JsonProperty("cdi")
    private Optional<CdiConfiguration> cdiConfig = Optional.empty();

    @Override
    public CdiConfiguration getCdiConfiguration() {
        return this.cdiConfig.orElse(null);
    }
}
