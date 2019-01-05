package com.cognodyne.dw.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.AnnotatedType;

public class CdiUtil {
    @SuppressWarnings("rawtypes")
    public static boolean isAnnotationPresent(AnnotatedType at, Class<? extends Annotation> cls) {
        if (at.isAnnotationPresent(cls)) {
            return true;
        }
        for (Type type : at.getTypeClosure()) {
            Class<?> typeClass = type.getClass();
            if (type instanceof Class) {
                typeClass = (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                typeClass = (Class<?>) ptype.getRawType();
            }
            if (typeClass.isAnnotationPresent(cls)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public static <T extends Annotation> T getAnnotation(AnnotatedType at, Class<T> cls) {
        if (at.isAnnotationPresent(cls)) {
            return at.getAnnotation(cls);
        }
        for (Type type : at.getTypeClosure()) {
            Class<?> typeClass = type.getClass();
            if (type instanceof Class) {
                typeClass = (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                typeClass = (Class<?>) ptype.getRawType();
            }
            if (typeClass.isAnnotationPresent(cls)) {
                return typeClass.getAnnotation(cls);
            }
        }
        return null;
    }
}
