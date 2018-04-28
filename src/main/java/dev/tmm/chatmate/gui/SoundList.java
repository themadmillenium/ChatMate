package dev.tmm.chatmate.gui;

import com.google.common.collect.Lists;
import dev.tmm.chatmate.compat.MinecraftClass;
import dev.tmm.chatmate.core.ChatSound;
import dev.tmm.chatmate.core.SoundRegistry;
import dev.tmm.chatmate.util.ChatColor;
import dev.tmm.chatmate.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SoundList extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiFilteredChat affectedChat;

    private GuiList list;

    public SoundList(GuiScreen screen, GuiFilteredChat affectedChat) {
        this.parentScreen = screen;
        this.affectedChat = affectedChat;
    }

    public void initGui() {
        this.buttonList.add(new GuiButton(5, this.width / 2 - 164, this.height - 38, 160, 20, "Play Selected Sound") {
            public void playPressSound(SoundHandler soundHandlerIn) {
            }
        });
        this.buttonList.add(new GuiButton(6, this.width / 2 + 4, this.height - 38, 160, 20, "Done"));
        this.list = new GuiList(this.mc, affectedChat.getSound());
        this.list.registerScrollButtons(7, 8);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            switch (button.id) {
                case 5:
                    MinecraftClass.getMinecraft().getPlayer().playSound((list.getSelectedSound()), 1, 1);
                    break;
                case 6:
                    String selectedSound = list.getSelectedSound();

                    if(selectedSound != null) affectedChat.setSound(new ChatSound(selectedSound, 1, 1));
                    mc.displayGuiScreen(parentScreen);
                    break;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.list.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, "Chat Notification Sounds", this.width / 2, 16, 16777215);
        if(!SoundRegistry.getKeySet().contains(affectedChat.getSound().getName())) this.drawCenteredString(this.fontRendererObj, "Warning: The currently selected sound is unavailable in this version.", this.width / 2, this.height - 56, 0xcca633);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @SideOnly(Side.CLIENT)
    private class GuiList extends GuiSlot {
        private final List<String> sounds;
        private final List<Boolean> isCategory;
        private final Map<Integer, String> categories;
        private int selectedSlot;

        private GuiList(Minecraft mcIn, ChatSound selectedSound) {
            super(mcIn, SoundList.this.width, SoundList.this.height, 32, SoundList.this.height - 65 + 4, 18);

            sounds = Lists.newArrayList();
            isCategory = Lists.newArrayList();

            for(String sound : SoundRegistry.getKeyList()) {
                sounds.add(sound);
                isCategory.add(false);
            }

            categories = SoundRegistry.getCategories();

            int increment = 0;

            ArrayList<Integer> sortedIndices = new ArrayList<>(categories.keySet());
            Collections.sort(sortedIndices);

            for(int i : sortedIndices) {
                sounds.add(i + increment, categories.get(i));
                isCategory.add(i + increment, true);

                if(i > 0) {
                    sounds.add(i + increment, "");
                    isCategory.add(i + increment, true);
                }

                increment += i > 0 ? 2 : 1;
            }

            selectedSlot = sounds.indexOf(selectedSound.getName());
        }

        protected int getSize() {
            return this.sounds.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            if (this.isCategory.get(slotIndex)) return;

            selectedSlot = slotIndex;
        }

        public String getSelectedSound() {
            if(selectedSlot == -1) return null;
            return this.sounds.get(selectedSlot);
        }

        protected boolean isSelected(int slotIndex) {
            return slotIndex == selectedSlot;
        }

        protected int getContentHeight() {
            return this.getSize() * 18;
        }

        protected void drawBackground() {
            SoundList.this.drawDefaultBackground();
        }

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            SoundList.this.drawCenteredString(SoundList.this.fontRendererObj, (this.isCategory.get(entryID) ? ChatColor.BOLD : "") + this.sounds.get(entryID), this.width / 2, p_180791_3_ + 1, this.isCategory.get(entryID) ? 0x79bedb : 0xffffff);
        }
    }
}
