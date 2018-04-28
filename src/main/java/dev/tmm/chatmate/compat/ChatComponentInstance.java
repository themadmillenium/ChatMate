package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatInstance;
import dev.tmm.chatmate.compat.base.CompatMethod;

public class ChatComponentInstance extends CompatInstance {
    public ChatComponentInstance(CompatInstance instance) {
        super(instance);
    }

    public ChatComponentInstance(Object instance) {
        super(instance);
    }

    public String getUnformattedText() {
        return (String) call(getUnformattedTextMethod).getBaseInstance();
    }

    public String getFormattedText() {
        return (String) call(getFormattedTextMethod).getBaseInstance();
    }

    public ChatComponentInstance setStyle(StyleInstance style) {
        call(setStyleMethod, style.getBaseInstance());

        return this;
    }

    public ChatComponentInstance appendSibling(ChatComponentInstance component) {
        call(appendSiblingMethod, component.getBaseInstance());

        return this;
    }

    private static final CompatMethod getUnformattedTextMethod = new CompatMethod(CompatUtility.clIChatComponent, "func_150260_c", "getUnformattedText").tryMapping();
    private static final CompatMethod getFormattedTextMethod = new CompatMethod(CompatUtility.clIChatComponent, "func_150254_d", "getFormattedText").tryMapping();
    private static final CompatMethod setStyleMethod = new CompatMethod(CompatUtility.clIChatComponent, "func_150255_a", "setStyle", "setChatStyle").tryMapping(CompatUtility.clStyle);

    private static final CompatMethod appendSiblingMethod = new CompatMethod(CompatUtility.clIChatComponent, "func_150257_a", "appendSibling").tryMapping();
}
