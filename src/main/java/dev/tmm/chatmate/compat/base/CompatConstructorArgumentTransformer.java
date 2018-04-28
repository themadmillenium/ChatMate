package dev.tmm.chatmate.compat.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface CompatConstructorArgumentTransformer<T> {
    CompatConstructorArgumentTransformer defaultConstructorTransformer = new CompatConstructorArgumentTransformer() {
        public Object newInstance(Constructor constructor, Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            return constructor.newInstance(args);
        }
    };

    T newInstance(Constructor constructor, Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException;
}
