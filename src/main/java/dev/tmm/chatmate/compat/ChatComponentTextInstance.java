package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatMethod;

public class ChatComponentTextInstance extends ChatComponentInstance {
    public ChatComponentTextInstance(Object instance) {
        super(instance);
    }

    public ChatComponentTextInstance(String text) {
        super(CompatUtility.clChatComponentText.newInstance(CompatUtility.ctorChatComponentText, text));
    }

    public String getChatComponentText_TextValue() {
        return (String) call(getChatComponentTextValueMethod).getBaseInstance();
    }

    private static final CompatMethod getChatComponentTextValueMethod = new CompatMethod(CompatUtility.clChatComponentText, "getChatComponentText_TextValue", "getText", "func_150265_g")
            .tryMapping();
}
