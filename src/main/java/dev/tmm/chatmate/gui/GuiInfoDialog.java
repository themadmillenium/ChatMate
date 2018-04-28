package dev.tmm.chatmate.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.List;

public class GuiInfoDialog extends GuiScreen {
    private GuiScreen parentScreen;
    private String messageLine1;
    private String messageLine2;
    private final List<String> field_175298_s = Lists.newArrayList();
    private boolean mousePressed;

    public GuiInfoDialog(GuiScreen parentScreen, String messageLine1, String messageLine2) {
        this.parentScreen = parentScreen;
        this.messageLine1 = messageLine1;
        this.messageLine2 = messageLine2;

        mousePressed = true;
    }

    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 96, 200, 20, "Okay"));
        this.field_175298_s.clear();
        this.field_175298_s.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        mc.displayGuiScreen(parentScreen);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mousePressed) return;

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        mousePressed = false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
        int i = 90;

        for (String s : this.field_175298_s) {
            this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
            i += this.fontRendererObj.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
