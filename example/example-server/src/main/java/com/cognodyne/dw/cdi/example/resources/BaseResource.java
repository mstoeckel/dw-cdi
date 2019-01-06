package com.cognodyne.dw.cdi.example.resources;

import javax.inject.Inject;

import com.cognodyne.dw.cdi.annotation.Configured;
import com.cognodyne.dw.cdi.example.DwCdiExampleConfiguration;

import io.dropwizard.Configuration;

public abstract class BaseResource {
    @Inject
    @Configured
    protected Configuration config;

    protected DwCdiExampleConfiguration getDwCdiExampleConfiguration() {
        return (DwCdiExampleConfiguration) this.config;
    }
}
