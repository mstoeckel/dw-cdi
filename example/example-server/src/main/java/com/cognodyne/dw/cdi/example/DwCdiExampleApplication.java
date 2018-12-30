package com.cognodyne.dw.cdi.example;

import javax.inject.Inject;

import com.cognodyne.dw.cdi.CdiBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DwCdiExampleApplication extends Application<DwCdiExampleConfiguration> {
    @Inject
    private CdiBundle cdiBundle;

    @Override
    public void initialize(Bootstrap<DwCdiExampleConfiguration> bootstrap) {
        bootstrap.addBundle(this.cdiBundle);
    }

    @Override
    public void run(DwCdiExampleConfiguration configuration, Environment environment) throws Exception {
        //environment.jersey().register(HelloResource.class);
    }

    public static void main(String... args) {
        try {
            CdiBundle.application(DwCdiExampleApplication.class, args).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
