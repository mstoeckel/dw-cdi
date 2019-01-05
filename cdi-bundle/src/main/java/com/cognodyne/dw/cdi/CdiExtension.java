package com.cognodyne.dw.cdi;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognodyne.dw.cdi.annotation.Startup;
import com.cognodyne.dw.cdi.exception.CircularDependencyDetectedException;
import com.cognodyne.dw.cdi.exception.UnsatisfiedDependencyException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import jersey.repackaged.com.google.common.collect.ImmutableList;

public class CdiExtension implements Extension {
    private static final Logger    logger         = LoggerFactory.getLogger(CdiExtension.class);
    private Set<AnnotatedType<?>>  annotatedTypes = Sets.newHashSet();
    private List<AnnotatedType<?>> startups;

    public Set<AnnotatedType<?>> getAnnotatedTypes() {
        return this.annotatedTypes;
    }

    public List<AnnotatedType<?>> getStartups() {
        return this.startups;
    }

    @SuppressWarnings("unused")
    private void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        logger.debug("beginning the scanning process");
    }

    @SuppressWarnings("unused")
    private <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        logger.debug("scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
        this.annotatedTypes.add(pat.getAnnotatedType());
    }

    @SuppressWarnings("unused")
    private void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        logger.debug("finished the scanning process");
        this.annotatedTypes = ImmutableSet.copyOf(this.annotatedTypes);
        this.startups = ImmutableList.copyOf(this.orderDependencies(this.annotatedTypes.stream()// 
                .filter((AnnotatedType<?> type) -> (type.isAnnotationPresent(ApplicationScoped.class) || type.isAnnotationPresent(Singleton.class)) && type.isAnnotationPresent(Startup.class))//
                .collect(Collectors.toList())));
//        WeldManager weldManager = (WeldManager)bm;
//        this.getAnnotatedTypes().stream()//
//        .filter(t -> t.isAnnotationPresent(Service.class))//
//        //.filter(t -> configuration.getCdiConfiguration() == null || configuration.getCdiConfiguration().include(t.getJavaClass()))//
//        .forEach(t -> weldManager.getServices().add((Class<org.jboss.weld.bootstrap.api.Service>) t.getAnnotation(Service.class).type(), (org.jboss.weld.bootstrap.api.Service) weldManager.instance().select(t.getJavaClass()).get()));
    }

    private <T> List<AnnotatedType<? extends T>> orderDependencies(List<AnnotatedType<? extends T>> list) {
        //first create a map of beans by class
        Map<Class<?>, AnnotatedType<? extends T>> types = Maps.newHashMap();
        for (AnnotatedType<? extends T> type : list) {
            types.put(type.getJavaClass(), type);
        }
        Graph<T> graph = new Graph<T>(types);
        //next we get the sorted node and turn it into list of beans
        return graph.getSorted().stream()//
                .map(t -> t.type)//
                .collect(Collectors.toList());
    }

    private static class Graph<T> {
        private Set<Node<T>> nodes = Sets.newHashSet();

        public Graph(Map<Class<?>, AnnotatedType<? extends T>> types) {
            Map<Class<?>, Node<T>> nodeMap = Maps.newHashMap();
            for (Entry<Class<?>, AnnotatedType<? extends T>> entry : types.entrySet()) {
                Node<T> node = nodeMap.get(entry.getKey());
                if (node == null) {
                    node = new Node<T>(entry.getValue());
                    nodeMap.put(entry.getKey(), node);
                }
                nodes.add(node);
                Startup anno = entry.getValue().getAnnotation(Startup.class);
                if (anno != null) {
                    for (Class<?> cls : anno.after()) {
                        AnnotatedType<? extends T> type = types.get(cls);
                        if (type == null) {
                            throw new UnsatisfiedDependencyException(cls + " not found");
                        }
                        Node<T> dependsOnNode = nodeMap.get(cls);
                        if (dependsOnNode == null) {
                            dependsOnNode = new Node<T>(type);
                            nodeMap.put(cls, dependsOnNode);
                        }
                        node.dependsOn.add(dependsOnNode);
                    }
                }
            }
        }

        // implementation of Kahn's topological sort algorithm  https://en.wikipedia.org/wiki/Topological_sorting
        public List<Node<T>> getSorted() {
            List<Node<T>> list = Lists.newArrayList();
            Set<Node<T>> set = nodes.stream().filter(node -> !hasIncomingEdge(node)).collect(Collectors.toSet());
            while (!set.isEmpty()) {
                Node<T> node = removeAny(set);
                list.add(node);
                for (Node<T> dependsOn : ImmutableSet.copyOf(node.dependsOn)) {
                    node.dependsOn.remove(dependsOn);
                    if (!hasIncomingEdge(dependsOn)) {
                        set.add(dependsOn);
                    }
                }
            }
            Optional<Node<T>> node = nodes.stream().filter(t -> !t.dependsOn.isEmpty()).findAny();
            if (node.isPresent()) {
                throw new CircularDependencyDetectedException("Circular dependency detected between " + node.get().type.getJavaClass() + " and " + node.get().dependsOn.stream()//
                        .map(t -> t.type.getJavaClass())//
                        .collect(Collectors.toSet()));
            }
            return Lists.reverse(list);
        }

        private boolean hasIncomingEdge(Node<T> n) {
            for (Node<T> node : nodes) {
                if (node.dependsOn.contains(n)) {
                    return true;
                }
            }
            return false;
        }

        private Node<T> removeAny(Set<Node<T>> set) {
            Node<T> node = set.iterator().next();
            set.remove(node);
            return node;
        }
    }

    private static class Node<T> {
        private AnnotatedType<? extends T> type;
        private Set<Node<T>>               dependsOn = Sets.newHashSet();

        private Node(AnnotatedType<? extends T> type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            return type.getJavaClass().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Node<?> other = (Node<?>) obj;
            return this.type.getJavaClass().equals(other.type.getJavaClass());
        }
    }
}
