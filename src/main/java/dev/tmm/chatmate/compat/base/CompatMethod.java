package dev.tmm.chatmate.compat.base;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CompatMethod {
    private CompatClass containingClass;

    private Method mappedMethod;
    private CompatMethodArgumentTransformer mappedDef;

    private String[] keys;

    public CompatMethod(CompatClass containingClass, String... keys) {
        this.containingClass = containingClass;
        this.keys = keys;
    }

    public CompatMethod tryMapping(CompatMethodArgumentTransformer def, CompatClass... argTypes) {
        Class<?>[] rawTypes = new Class[argTypes.length];

        for (int i = 0; i < argTypes.length; i++) {
            rawTypes[i] = argTypes[i].getMappedClass();
        }

        try {
            mappedMethod = ReflectionHelper.findMethod(containingClass.getMappedClass(), null, keys, rawTypes);
            mappedDef = def;
        } catch (ReflectionHelper.UnableToFindMethodException ignored) {
        }

        return this;
    }

    public CompatMethod tryMapping(CompatClass... argTypes) {
        return tryMapping(CompatMethodArgumentTransformer.defaultMethodTransformer, argTypes);
    }

    public Object call(Object instance, Object... args) {
        try {
            return mappedDef.run(mappedMethod, instance, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Method getMappedMethod() {
        return mappedMethod;
    }

    public boolean mappingExists() {
        return mappedMethod != null;
    }
}