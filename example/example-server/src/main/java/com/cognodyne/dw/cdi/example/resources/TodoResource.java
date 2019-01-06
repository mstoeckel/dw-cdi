package com.cognodyne.dw.cdi.example.resources;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.annotation.Startup;
import com.cognodyne.dw.example.api.model.Todo;
import com.cognodyne.dw.example.api.service.TodoService;

@Singleton
@Startup
public class TodoResource extends BaseResource implements TodoService {
    private static final Logger logger = LoggerFactory.getLogger(TodoResource.class);
    private Server              h2WebServer;
    @PersistenceContext(unitName = "exampleUnit")
    private EntityManager       em;

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
        logger.debug("job:{}", job);
        this.em.merge(job);
    }

    @Override
    public void update(Todo job) {
        // TODO Auto-generated method stub
    }

    @PostConstruct
    public void onInit() {
        logger.debug("onInit...");
        try {
            this.h2WebServer = Server.createWebServer("-webAllowOthers", "-webPort", "8082").start();
            logger.debug("h2 Web server started at http://localhost:8082");
        } catch (SQLException e) {
            logger.error("Unable to start h2 webserver", e);
        }
    }

    @PreDestroy
    public void onDestroy() {
        logger.debug("onDestroy...");
        if (this.h2WebServer != null) {
            this.h2WebServer.stop();
        }
    }
}
