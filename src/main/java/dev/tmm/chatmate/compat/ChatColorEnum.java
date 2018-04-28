package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatInstance;

public class ChatColorEnum extends CompatInstance {
    public static ChatColorEnum valueOf(String name) {
        return new ChatColorEnum(Enum.valueOf(CompatUtility.clChatColor.getMappedClass(), name));
    }

    private ChatColorEnum(Enum color) {
        super(color);
    }
}
