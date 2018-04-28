package dev.tmm.chatmate.compat;

import dev.tmm.chatmate.compat.base.CompatClass;
import dev.tmm.chatmate.compat.base.CompatMethod;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

public class GuiUtilRenderComponentsClass extends CompatClass {
    public static final GuiUtilRenderComponentsClass instance = new GuiUtilRenderComponentsClass();

    public static List<ChatComponentInstance> splitText(ChatComponentInstance chatComponent, int maxTextLength, FontRenderer renderer, boolean b, boolean forceTextColor) {
        List<?> chatComponents = (List<?>) instance.call(splitTextMethod, chatComponent.getBaseInstance(), maxTextLength, renderer, b, forceTextColor).getBaseInstance();

        ArrayList<ChatComponentInstance> compatList = new ArrayList<>();
        for (Object o : chatComponents) {
            compatList.add(new ChatComponentInstance(o));
        }

        return compatList;
    }

    private static CompatMethod splitTextMethod = new CompatMethod(instance, "func_178908_a", "splitText")
            .tryMapping(CompatUtility.clIChatComponent, CompatUtility.clInt, CompatUtility.clFontRenderer, CompatUtility.clBoolean, CompatUtility.clBoolean);

    private GuiUtilRenderComponentsClass() {
        super("net.minecraft.client.gui.GuiUtilRenderComponents");
    }
}
