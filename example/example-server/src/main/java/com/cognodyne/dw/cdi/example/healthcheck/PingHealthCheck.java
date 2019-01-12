package com.cognodyne.dw.cdi.example.healthcheck;

import javax.inject.Named;

import com.codahale.metrics.health.HealthCheck;

@Named("ping")
public class PingHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
