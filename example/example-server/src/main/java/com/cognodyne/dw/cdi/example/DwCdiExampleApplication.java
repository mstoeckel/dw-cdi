package com.cognodyne.dw.cdi.example;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.CdiBundle;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DwCdiExampleApplication extends Application<DwCdiExampleConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(DwCdiExampleApplication.class);
    @Inject
    private CdiBundle           cdiBundle;

    @Override
    public void initialize(Bootstrap<DwCdiExampleConfiguration> bootstrap) {
        logger.debug("initializing...");
        bootstrap.addBundle(this.cdiBundle);
    }

    @Override
    public void run(DwCdiExampleConfiguration configuration, Environment environment) throws Exception {
        logger.debug("running...");
    }

    public static void main(String... args) {
        try {
            CdiBundle.application(DwCdiExampleApplication.class)//
                    .withEE()//
                    .start(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
