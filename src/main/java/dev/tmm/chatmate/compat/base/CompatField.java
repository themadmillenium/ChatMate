package dev.tmm.chatmate.compat.base;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class CompatField {
    private Field mappedField;

    public CompatField(CompatClass containingClass, String... keys) {
        try {
            mappedField = ReflectionHelper.findField(containingClass.getMappedClass(), keys);
        } catch(ReflectionHelper.UnableToFindFieldException ignored) {}
    }

    public Object get(Object instance) {
        try {
            return mappedField.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(Object instance, Object val) {
        try {
            mappedField.set(instance, val);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean mappingExists() {
        return mappedField != null;
    }
}
