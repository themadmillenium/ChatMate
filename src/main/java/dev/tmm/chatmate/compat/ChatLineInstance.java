package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatInstance;
import dev.tmm.chatmate.compat.base.CompatMethod;

public class ChatLineInstance extends CompatInstance {
    public ChatLineInstance(int updateCounter, ChatComponentInstance iTextComponent, int chatLineId) {
        super(CompatUtility.clChatLine.newInstance(CompatUtility.ctorChatLine, updateCounter, iTextComponent.getBaseInstance(), chatLineId));
    }

    public int getChatLineID() {
        return (Integer) call(getChatLineIDMethod).getBaseInstance();
    }

    public ChatComponentInstance getChatComponent() {
        return new ChatComponentInstance(call(getChatComponentMethod));
    }

    public int getUpdatedCounter() {
        return (Integer) call(getUpdatedCounterMethod).getBaseInstance();
    }

    private static final CompatMethod getChatLineIDMethod = new CompatMethod(CompatUtility.clChatLine, "func_74539_c", "getChatLineID").tryMapping();
    private static final CompatMethod getChatComponentMethod = new CompatMethod(CompatUtility.clChatLine, "func_151461_a", "getChatComponent").tryMapping();
    private static final CompatMethod getUpdatedCounterMethod = new CompatMethod(CompatUtility.clChatLine, "func_74540_b", "getUpdatedCounter").tryMapping();
}