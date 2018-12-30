package com.cognodyne.dw.example.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.codahale.metrics.annotation.Timed;

@Path("/hello")
public interface HelloService {
    @GET
    @Timed
    public String sayHello();
}
