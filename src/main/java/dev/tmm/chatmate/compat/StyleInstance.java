package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatInstance;
import dev.tmm.chatmate.compat.base.CompatMethod;

public class StyleInstance extends CompatInstance {
    public StyleInstance() {
        super(CompatUtility.clStyle.newInstance(CompatUtility.ctorStyle));
    }

    public void setBold(boolean bold) {
        call(setBoldMethod, bold);
    }

    public void setItalic(boolean italic) {
        call(setItalicMethod, italic);
    }

    public void setObfuscated(boolean obfuscated) {
        call(setObfuscatedMethod, obfuscated);
    }

    public void setStrikethrough(boolean strikethrough) {
        call(setStrikethroughMethod, strikethrough);
    }

    public void setUnderlined(boolean underlined) {
        call(setUnderlinedMethod, underlined);
    }

    public void setColor(ChatColorEnum color) {
        call(setColorMethod, color.getBaseInstance());
    }

    private static final CompatMethod setBoldMethod = new CompatMethod(CompatUtility.clStyle, "func_150227_a", "setBold").tryMapping(CompatUtility.clBooleanWrapper);
    private static final CompatMethod setItalicMethod = new CompatMethod(CompatUtility.clStyle, "func_150217_b", "setItalic").tryMapping(CompatUtility.clBooleanWrapper);
    private static final CompatMethod setObfuscatedMethod = new CompatMethod(CompatUtility.clStyle, "func_150237_e", "setObfuscated").tryMapping(CompatUtility.clBooleanWrapper);
    private static final CompatMethod setStrikethroughMethod = new CompatMethod(CompatUtility.clStyle, "func_150225_c", "setStrikethrough").tryMapping(CompatUtility.clBooleanWrapper);
    private static final CompatMethod setUnderlinedMethod = new CompatMethod(CompatUtility.clStyle, "func_150228_d", "setUnderlined").tryMapping(CompatUtility.clBooleanWrapper);
    private static final CompatMethod setColorMethod = new CompatMethod(CompatUtility.clStyle, "func_150238_a", "setColor").tryMapping(CompatUtility.clChatColor);
}
