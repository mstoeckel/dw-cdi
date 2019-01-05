package com.cognodyne.dw.example.api.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.cognodyne.dw.example.api.model.Todo;

@Path("/todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TodoService {
    @GET
    @Timed
    @Path("/{id}")
    public Todo get(@PathParam("id") String id);

    @GET
    @Path("/all")
    @Timed
    public List<Todo> getAll();

    @PUT
    @Timed
    public void create(Todo job);

    @POST
    @Timed
    public void update(Todo job);
}
