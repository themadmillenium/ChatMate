package dev.tmm.chatmate.compat.base;

public class CompatInstance {
    private final Object instance;

    public CompatInstance(CompatInstance instance) {
        this.instance = instance.getBaseInstance();
    }

    public CompatInstance(Object instance) {
        this.instance = instance;
    }

    public CompatInstance get(CompatField field) {
        return new CompatInstance(field.get(instance));
    }

    public void set(CompatField field, Object val) {
        field.set(instance, val);
    }

    public CompatInstance call(CompatMethod method, Object... args) {
        return new CompatInstance(method.call(instance, args));
    }

    public Object getBaseInstance() {
        return instance;
    }
}
