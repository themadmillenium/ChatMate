package dev.tmm.chatmate.compat.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CompatConstructor {
    private CompatClass containingClass;

    private Constructor mappedCtor;
    private CompatConstructorArgumentTransformer mappedDef;

    public CompatConstructor(CompatClass containingClass) {
        this.containingClass = containingClass;
    }

    public CompatConstructor tryMapping(CompatConstructorArgumentTransformer def, CompatClass... argTypes) {
        if(containingClass.getMappedClass() == null) return this;

        Class<?>[] rawTypes = new Class[argTypes.length];

        for (int i = 0; i < argTypes.length; i++) {
            rawTypes[i] = argTypes[i].getMappedClass();
        }

        try {
            mappedCtor = getConstructor(rawTypes);
            mappedDef = def;
        } catch (NoSuchMethodException ignored) {
        }

        return this;
    }

    public CompatConstructor tryMapping(CompatClass... argTypes) {
        return tryMapping(CompatConstructorArgumentTransformer.defaultConstructorTransformer, argTypes);
    }

    public Object newInstance(Object... args) {
        try {
            return mappedDef.newInstance(mappedCtor, args);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private Constructor getConstructor(Class... argTypes) throws NoSuchMethodException {
        if(containingClass.getMappedClass() == null) throw new NoSuchMethodException();

        loop:
        for (Constructor c : containingClass.getMappedConstructors()) {
            Class<?>[] clArr = c.getParameterTypes();

            if (clArr.length != argTypes.length) continue;

            for (int i = 0; i < clArr.length; i++) {
                if (clArr[i].isAssignableFrom(argTypes[i])) continue;
                if (arePrimitivesEqual(clArr[i], argTypes[i])) continue;
                continue loop;
            }

            return c;
        }

        throw new NoSuchMethodException();
    }

    private boolean arePrimitivesEqual(Class<?> a, Class<?> b) {
        if (isInteger(a) && isInteger(b)) return true;
        if (isFloat(a) && isFloat(b)) return true;
        if (isDouble(a) && isDouble(b)) return true;
        if (isLong(a) && isLong(b)) return true;
        if (isByte(a) && isByte(b)) return true;
        if (isShort(a) && isShort(b)) return true;
        if (isChar(a) && isChar(b)) return true;
        if (isBoolean(a) && isBoolean(b)) return true;

        return false;
    }

    private static boolean isInteger(Class<?> c) {
        return c.equals(Integer.class) || c.equals(int.class);
    }

    private static boolean isFloat(Class<?> c) {
        return c.equals(Float.class) || c.equals(float.class);
    }

    private static boolean isDouble(Class<?> c) {
        return c.equals(Double.class) || c.equals(double.class);
    }

    private static boolean isLong(Class<?> c) {
        return c.equals(Long.class) || c.equals(long.class);
    }

    private static boolean isByte(Class<?> c) {
        return c.equals(Byte.class) || c.equals(byte.class);
    }

    private static boolean isShort(Class<?> c) {
        return c.equals(Short.class) || c.equals(short.class);
    }

    private static boolean isChar(Class<?> c) {
        return c.equals(Character.class) || c.equals(char.class);
    }

    private static boolean isBoolean(Class<?> c) {
        return c.equals(Boolean.class) || c.equals(boolean.class);
    }

    public Constructor getMappedConstructor() {
        return mappedCtor;
    }

    public boolean mappingExists() {
        return mappedCtor != null;
    }
}
