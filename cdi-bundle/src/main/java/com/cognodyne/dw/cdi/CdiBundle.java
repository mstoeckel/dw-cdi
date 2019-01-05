package com.cognodyne.dw.cdi;

import java.util.Arrays;
import java.util.EnumSet;

import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Path;
import javax.ws.rs.container.DynamicFeature;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.environment.servlet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.cognodyne.dw.cdi.annotation.Configured;
import com.cognodyne.dw.cdi.service.JpaServiceProvider;
import com.cognodyne.dw.cdi.service.JtaServiceProvider;
import com.cognodyne.dw.cdi.service.ResourceInjectionServiceProvider;
import com.cognodyne.dw.cdi.service.SecurityServiceProvider;

import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.cli.Command;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

@Singleton
public class CdiBundle implements ConfiguredBundle<CdiConfigurable> {
    private static final Logger logger = LoggerFactory.getLogger(CdiBundle.class);
    @Inject
    private CdiExtension        extension;
    @Inject
    private BeanManager         bm;
    private CdiConfigurable     configuration;
    private Environment         environment;

    public static <T extends Application<?>> ApplicationStarter<T> application(Class<T> appClass) {
        return new ApplicationStarter<T>(appClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        logger.debug("initializing...");
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> Command.class.isAssignableFrom(t.getJavaClass()))//
                .forEach(t -> {
                    Command cmd = CDI.current().select((Class<? extends Command>) t.getJavaClass()).get();
                    if (cmd != null) {
                        bootstrap.addCommand(cmd);
                    }
                });
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> ConfiguredCommand.class.isAssignableFrom(t.getJavaClass()))//
                .forEach(t -> {
                    ConfiguredCommand<?> cmd = CDI.current().select((Class<? extends ConfiguredCommand<?>>) t.getJavaClass()).get();
                    if (cmd != null) {
                        bootstrap.addCommand(cmd);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run(CdiConfigurable configuration, Environment environment) throws Exception {
        this.configuration = configuration;
        this.environment = environment;
        logger.debug("running with bm:{}, configuration:{}, environment:{}...", this.bm, this.configuration, this.environment);
        environment.getApplicationContext().addEventListener(Listener.using(this.bm));
        //register healthchecks
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> HealthCheck.class.isAssignableFrom(t.getJavaClass()))//
                .filter(t -> CdiUtil.isAnnotationPresent(t, Named.class))//
                .filter(t -> shouldInclude(configuration, t))//
                .forEach(t -> environment.healthChecks().register(CdiUtil.getAnnotation(t, Named.class).value(), (HealthCheck) CDI.current().select(t.getJavaClass()).get()));
        //register managed
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> Managed.class.isAssignableFrom(t.getJavaClass()))//
                .filter(t -> shouldInclude(configuration, t))//
                .forEach(t -> environment.lifecycle().manage((Managed) CDI.current().select(t.getJavaClass()).get()));
        //register tasks
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> Task.class.isAssignableFrom(t.getJavaClass()))//
                .filter(t -> shouldInclude(configuration, t))//
                .forEach(t -> environment.admin().addTask((Task) CDI.current().select(t.getJavaClass()).get()));
        //register dynamic features
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> DynamicFeature.class.isAssignableFrom(t.getJavaClass()))//
                .filter(t -> shouldInclude(configuration, t))//
                .forEach(t -> environment.jersey().register(CDI.current().select(t.getJavaClass()).get()));
        //register servlet filters
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> Filter.class.isAssignableFrom(t.getJavaClass()))//
                .filter(t -> CdiUtil.isAnnotationPresent(t, WebFilter.class))//
                .filter(t -> shouldInclude(configuration, t))//
                .sorted((lo, ro) -> {
                    int lhs = CdiUtil.isAnnotationPresent(lo, Priority.class) ? CdiUtil.getAnnotation(lo, Priority.class).value() : Integer.MAX_VALUE;
                    int rhs = CdiUtil.isAnnotationPresent(ro, Priority.class) ? CdiUtil.getAnnotation(ro, Priority.class).value() : Integer.MAX_VALUE;
                    return lhs - rhs;
                }).forEach(t -> {
                    WebFilter anno = CdiUtil.getAnnotation(t, WebFilter.class);
                    javax.servlet.FilterRegistration.Dynamic filter = environment.servlets().addFilter(anno.filterName(), (Class<Filter>) t.getJavaClass());
                    if (anno.urlPatterns() != null && anno.urlPatterns().length != 0) {
                        filter.addMappingForUrlPatterns(EnumSet.copyOf(Arrays.asList(anno.dispatcherTypes())), true, anno.urlPatterns());
                    } else if (anno.value() != null && anno.value().length != 0) {
                        filter.addMappingForUrlPatterns(EnumSet.copyOf(Arrays.asList(anno.dispatcherTypes())), true, anno.value());
                    } else if (anno.servletNames() != null && anno.servletNames().length != 0) {
                        filter.addMappingForUrlPatterns(EnumSet.copyOf(Arrays.asList(anno.dispatcherTypes())), true, anno.servletNames());
                    }
                    filter.setAsyncSupported(anno.asyncSupported());
                    if (anno.initParams() != null && anno.initParams().length != 0) {
                        for (WebInitParam param : anno.initParams()) {
                            filter.setInitParameter(param.name(), param.value());
                        }
                    }
                });
        //register servlets
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> Servlet.class.isAssignableFrom(t.getJavaClass()))//
                .filter(t -> CdiUtil.isAnnotationPresent(t, WebServlet.class))//
                .filter(t -> shouldInclude(configuration, t))//
                .forEach(t -> {
                    WebServlet anno = CdiUtil.getAnnotation(t, WebServlet.class);
                    javax.servlet.ServletRegistration.Dynamic servlet = environment.servlets().addServlet(anno.name(), (Servlet) CDI.current().select(t.getJavaClass()).get());
                    if (anno.urlPatterns() != null && anno.urlPatterns().length != 0) {
                        servlet.addMapping(anno.urlPatterns());
                    } else if (anno.value() != null && anno.value().length != 0) {
                        servlet.addMapping(anno.value());
                    }
                    servlet.setAsyncSupported(anno.asyncSupported());
                    servlet.setLoadOnStartup(anno.loadOnStartup());
                    if (anno.initParams() != null && anno.initParams().length != 0) {
                        for (WebInitParam param : anno.initParams()) {
                            servlet.setInitParameter(param.name(), param.value());
                        }
                    }
                });
        //register jersey resources
        this.extension.getAnnotatedTypes().stream()//
                .filter(t -> CdiUtil.isAnnotationPresent(t, Path.class))//
                .filter(t -> this.shouldInclude(configuration, t))//
                .forEach(t -> environment.jersey().register(t.getJavaClass()));
        // start startups
        this.extension.getStartups().stream()//
                .forEach(t -> CDI.current().select(t.getJavaClass()).get());
    }

    private boolean shouldInclude(CdiConfigurable configuration, AnnotatedType<?> type) {
        return !configuration.getCdiConfiguration().isPresent() || configuration.getCdiConfiguration().get().include(type.getJavaClass());
    }

    @Produces
    @Configured
    public CdiConfigurable getConfiguration() {
        logger.debug("returning configuaration:{}", this.configuration);
        return configuration;
    }

    @Produces
    @Configured
    public Environment getEnvironment() {
        logger.debug("returning environment:{}", this.environment);
        return environment;
    }

    public static final class ApplicationStarter<T extends Application<?>> {
        private Class<T> cls;
        private boolean  withEE = false;

        private ApplicationStarter(Class<T> cls) {
            this.cls = cls;
        }

        public ApplicationStarter<T> withEE() {
            this.withEE = true;
            return this;
        }

        public void start(String... args) throws Exception {
            Weld weld = new Weld();
            if (this.withEE) {
                weld.addServices(new JpaServiceProvider(), new JtaServiceProvider(), new ResourceInjectionServiceProvider(), new SecurityServiceProvider());
            }
            WeldContainer container = weld.initialize();
            container.select(this.cls).get().run(args);
        }
    }
}
