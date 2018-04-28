package dev.tmm.chatmate.compat.base;

import java.lang.reflect.Constructor;

public class CompatClass {
    private final Class<?> mappedClass;
    private final Constructor[] mappedCtors;

    public CompatClass(String... keys) {
        mappedClass = tryGetClass(keys);

        if (mappedClass == null) {
            mappedCtors = null;
        } else {
            mappedCtors = mappedClass.getConstructors();
        }
    }

    public CompatClass(Class c) {
        mappedClass = c;

        if (mappedClass == null) {
            mappedCtors = null;
        } else {
            mappedCtors = mappedClass.getConstructors();
        }
    }

    public boolean isInstance(Object in) {
        return mappedClass.isAssignableFrom(in.getClass());
    }

    public CompatInstance newInstance(CompatConstructor ctor, Object... args) {
        return new CompatInstance(ctor.newInstance(args));
    }

    public CompatInstance get(CompatField field) {
        return new CompatInstance(field.get(null));
    }

    public void set(CompatField field, Object val) {
        field.set(null, val);
    }

    public CompatInstance call(CompatMethod method, Object... args) {
        return new CompatInstance(method.call(null, args));
    }

    public Class getMappedClass() {
        return mappedClass;
    }

    private Class tryGetClass(String... keys) {
        for (String key : keys) {
            try {
                return Class.forName(key);
            } catch (ClassNotFoundException ignored) {
            }
        }

        return null;
    }

    public Constructor[] getMappedConstructors() {
        return mappedCtors;
    }

    public boolean mappingExists() {
        return mappedClass != null;
    }
}
