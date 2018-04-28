package dev.tmm.chatmate.compat.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface CompatMethodArgumentTransformer<T> {
    CompatMethodArgumentTransformer defaultMethodTransformer = new CompatMethodArgumentTransformer() {
        public Object run(Method method, Object instance, Object... args) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(instance, args);
        }
    };

    T run(Method method, Object instance, Object... args) throws InvocationTargetException, IllegalAccessException;
}
