package com.cognodyne.dw.cdi.example.resources;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.annotation.Startup;
import com.cognodyne.dw.example.api.model.Todo;
import com.cognodyne.dw.example.api.service.TodoService;

@Singleton
@Startup
public class TodoResource implements TodoService {
    private static final Logger logger = LoggerFactory.getLogger(TodoResource.class);

    @Override
    public Todo get(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Todo> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(Todo job) {
        // TODO Auto-generated method stub
    }

    @Override
    public void update(Todo job) {
        // TODO Auto-generated method stub
    }

    @PostConstruct
    public void onInit() {
        logger.debug("onInit...");
    }

    @PreDestroy
    public void onDestroy() {
        logger.debug("onDestroy...");
    }
}
